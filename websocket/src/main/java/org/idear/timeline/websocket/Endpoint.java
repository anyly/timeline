package org.idear.timeline.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class Endpoint extends javax.websocket.Endpoint {
    protected GameCenter gameCenter;
    protected Session session;
    protected String user;
    protected String img;


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        load(config);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String action = jsonObject.getString("action");
                Object data = jsonObject.get("data");
                String httpPrefix = "http_";
                if (action.startsWith(httpPrefix)) {
                    // http请求
                    String newAction = action.substring(httpPrefix.length());
                    Object response = messageHandler(newAction, data);
                    if (response == null) {
                        response = httpResponse(newAction, data);
                    }
                    emit(action, response);
                } else {
                    // admit请求
                    messageHandler(action, data);
                }
            }
        });
    }

    private void load(EndpointConfig config) {
//        String path = this.session.getRequestURI().getPath();
//        String string = path;
//        int index = path.lastIndexOf('/');
//        if (index > 0) {
//            string = path.substring(index + 1);
//        }
//        int no = Integer.valueOf(string);
        gameCenter = (GameCenter) config.getUserProperties().get(GameCenter.class.getName());
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
    private Object messageHandler(String action, Object data) {
        String methodName = methodName(action);
//        if (methodName.equals("onLogin")) {
//            return onLogin(data);
//        } else if (methodName.equals("onLogout")) {
//            return onLogout();
//        }

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
                        if (session != null && !set.contains(session) &&
                                (session.getClass() == parameterType
                                || parameterType.isAssignableFrom(session.getClass()))) {
                            parameter.add(session);
                            set.add(session);
                        } else if (action != null && !set.contains(action) &&
                                (action.getClass() == parameterType
                                || parameterType.isAssignableFrom(action.getClass()))) {
                            parameter.add(action);
                            set.add(action);
                        } else if (data != null && !set.contains(data) &&
                                (data.getClass() == parameterType
                                || parameterType.isAssignableFrom(data.getClass()))) {
                            parameter.add(data);
                            set.add(data);
                        } else {
                            parameter.add(null);
                        }
                    }

                    Object returnObject = method.invoke(this, parameter.toArray());
                    return returnObject;
                }

            }

        } catch (ClassCastException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("找不到"+methodName+"()方法！");
        return null;
    }

    protected Object httpResponse(String action, Object data) {
        return null;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        onLogout();
        gameCenter = null;
        this.session = null;
    }

    @Override
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
        thr.printStackTrace();
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定某一端传输
     * @param action
     * @param data
     */
    final public void emit(String action, Object data){
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
    ///////////getter setter/////////////
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            session.close();
        } catch (Exception e) {

        }
    }
////////业务逻辑////////////
    /**
     * 登录
     * @param data
     * @return
     */
    public JSONObject onLogin(JSONObject data) {
        user = data.getString("user");
        img = data.getString("img");
        gameCenter.login(this);
        return data;
    }

    /**
     * 登出
     * @return
     */
    public String onLogout() {
        String u = user;
        try {
            gameCenter.logout(user);
            user = null;
            img = null;
        } catch (Exception e) {

        }
        return u;
    }


    /**
     * 创建游戏
     * @return
     */
    public int onNewGame(JSONObject data) {
        int no = gameCenter.newGame(data).getNo();
        return no;
    }

    /**
     * 删除游戏
     */
    public void onRemoveGame(Integer no) {
        gameCenter.removeGame(no);
    }
}
