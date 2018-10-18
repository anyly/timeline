package org.idear.timeline.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class Endpoint extends javax.websocket.Endpoint {
    protected Hall hall;
    protected Room room;
    protected Player player;
    protected Session session;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        load(config);
        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            JSONObject jsonObject = JSONObject.parseObject(message);
            String action = jsonObject.getString("action");
            JSONObject data = jsonObject.getJSONObject("data");
            String httpPrefix = "http_";
            if (action.startsWith(httpPrefix)) {
                // http请求
                String newAction = action.substring(httpPrefix.length());
                JSONObject response = messageHandler(newAction, data);
                if (response == null) {
                    response = httpResponse(newAction, data);
                }
                emit(action, response);
            } else {
                // admit请求
                messageHandler(action, data);
            }
        });
    }

    private void load(EndpointConfig config) {
        String path = this.session.getRequestURI().getPath();
        String string = path;
        int index = path.lastIndexOf('/');
        if (index > 0) {
            string = path.substring(index + 1);
        }
        int no = Integer.valueOf(string);
        hall = (Hall) config.getUserProperties().get(Hall.class.getName());
        room = hall.room(no);
    }

    public JSONObject onLogin(JSONObject data) {
        String user = data.getString("user");
        String img = data.getString("img");
        player = room.player(user);
        if (player != null) {
            try {
                player.endpoint().session.close();
            } catch (Exception e) {

            }
        } else {
            player = new Player();
            player.setUser(user);
            player.setImg(img);
            room.join(player);
        }
        player.endpoint(this);
        return null;
    }

    public JSONObject onLogout() {
        if (player != null) {
            room.leave(player);
            try {
                player.endpoint().session.close();
            } catch (Exception e) {

            }
            player.endpoint(null);
        }
        return null;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        if (player != null) {
            player.endpoint(null);
        }

    }

    private String methodName(String s){
        if (s.length() == 0) {
            return s;
        }
        if(Character.isUpperCase(s.charAt(0))) {
            return "on"+s;
        } else {
            return "on"+Character.toUpperCase(s.charAt(0))+s.substring(1);
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject messageHandler(String action, JSONObject data) {
        String methodName = methodName(action);
        if (methodName.equals("onLogin")) {
            return onLogin(data);
        } else if (methodName.equals("onLogout")) {
            return onLogout();
        }

        try {
            Class thisClass = this.getClass();
            Method[] methods = thisClass.getMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {// 方法名一致
                    Class returnType = method.getReturnType();
                    if (returnType != null && (
                            returnType.isAssignableFrom(Array.class) ||
                                    returnType.isAssignableFrom(Set.class) ||
                                    returnType.isAssignableFrom(List.class))) {
                        throw new ClassCastException(methodName+"() return type is not allow Array/Set/List");
                    }
                    Class[] parameterTypes = method.getParameterTypes();
                    Set set = new HashSet();
                    ArrayList parameter = new ArrayList(parameterTypes.length);
                    for (Class parameterType : parameterTypes) {
                        if (!set.contains(session) &&
                                (session.getClass() == parameterType
                                || parameterType.isAssignableFrom(session.getClass()))) {
                            parameter.add(session);
                            set.add(session);
                        } else if (!set.contains(action) &&
                                (action.getClass() == parameterType
                                || parameterType.isAssignableFrom(action.getClass()))) {
                            parameter.add(action);
                            set.add(action);
                        } else if (!set.contains(data) &&
                                (data.getClass() == parameterType
                                || parameterType.isAssignableFrom(data.getClass()))) {
                            parameter.add(data);
                            set.add(data);
                        } else {
                            parameter.add(null);
                        }
                    }

                    // 反射不支持不定数组
                    //Object returnObject = method.invoke(this, session, data);
                    Object returnObject = method.invoke(this, parameter.toArray());
                    if (returnObject == null) {
                        return null;
                    } else if (returnObject instanceof JSONObject) {
                        return (JSONObject) returnObject;
                    } else {
                        String jsonString = JSON.toJSONString(returnObject, SerializerFeature.DisableCircularReferenceDetect);
                        return (JSONObject) JSONObject.parse(jsonString);
                    }
                }

            }

        } catch (ClassCastException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("找不到"+methodName+"()方法！");
        return null;
    }

    protected JSONObject httpResponse(String action, JSONObject data) {
        return null;
    }

    /**
     * 指定某一端传输
     * @param action
     * @param data
     */
    final public void emit(String action, JSONObject data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);
        try {
            String message = JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
