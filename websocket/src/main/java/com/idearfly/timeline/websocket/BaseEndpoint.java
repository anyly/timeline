package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.idearfly.timeline.websocket.annotation.RequireLogin;
import com.idearfly.utils.GenericUtils;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseEndpoint<GameCenter extends BaseGameCenter> extends javax.websocket.Endpoint {
    private Class<GameCenter> gameCenterClass;
    protected GameCenter gameCenter;
    protected Session session;
    protected String user;
    protected String img;

    public BaseEndpoint() {
        gameCenterClass = GenericUtils.fromSuperclass(this.getClass(), BaseGameCenter.class);
        if (gameCenterClass == null) {
            gameCenterClass = (Class<GameCenter>) DefaultGameCenter.class;
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        Log.debug("open session", session.getId());
        this.session = session;
        load(config);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String action = jsonObject.getString("action");
                Object data = jsonObject.get("data");
                try {
                    String httpPrefix = "http_";
                    if (action.startsWith(httpPrefix)) {
                        // http请求
                        String newAction = action.substring(httpPrefix.length());
                        Object response = messageHandler(newAction, data);
                        emit(action, response);
                    } else {
                        // admit请求
                        messageHandler(action, data);
                    }
                } catch (RequireLoginException e) {
                    emit("requireLogin", null);
                }

            }
        });
    }

    private void load(EndpointConfig config) {
        gameCenter = (GameCenter) config.getUserProperties().get(gameCenterClass.getName());
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

        try {
            Class thisClass = this.getClass();
            Method[] methods = thisClass.getMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {// 方法名一致
//                    Class returnType = method.getReturnType();
//                    if (returnType != null && (
//                            returnType.isAssignableFrom(Array.class) ||
//                                    returnType.isAssignableFrom(Set.class) ||
//                                    returnType.isAssignableFrom(List.class))) {
//                        throw new ClassCastException(methodName+"() return type is not allow Array/Set/List");
//                    }
                    RequireLogin requireLogin = method.getAnnotation(RequireLogin.class);
                    if (requireLogin != null) {
                        if (requireLogin()) {
                            throw new RequireLoginException();
                        }
                    }

                    Class[] parameterTypes = method.getParameterTypes();
                    Set set = new HashSet();
                    ArrayList parameter = new ArrayList(parameterTypes.length);
                    boolean warning = true;
                    for (Class parameterType : parameterTypes) {
                        if (session != null && !set.contains(session) &&
                                (session.getClass() == parameterType
                                || parameterType.isAssignableFrom(session.getClass()))) {
                            parameter.add(session);
                            set.add(session);
                            warning = false;
//                        } else if (action != null && !set.contains(action) &&
//                                (action.getClass() == parameterType
//                                || parameterType.isAssignableFrom(action.getClass()))) {
//                            parameter.add(action);
//                            set.add(action);
//                            warning = false;
                        } else if (data != null && !set.contains(data) &&
                                (data.getClass() == parameterType
                                || parameterType.isAssignableFrom(data.getClass()))) {
                            parameter.add(data);
                            set.add(data);
                            warning = false;
                        } else {
                            parameter.add(null);
                        }
                    }

                    if (warning) {
                        Log.debug(methodName + " has not match " + JSON.toJSONString(parameterTypes));
                    }
                    Object[] array = parameter.toArray();
                    Object returnObject = method.invoke(this, array);
                    Log.debug("onMessage", methodName+" invoke", array);
                    return returnObject;
                }

            }

        } catch (ClassCastException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.debug("onMessage", "找不到"+methodName+"()方法！");
        return null;
    }

    protected boolean requireLogin() {
        return user == null;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        onLogout();
        Log.debug("onClose session", session.getId());
        gameCenter = null;
        this.session = null;
    }

    @Override
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
        try {
            Log.debug("onError session", session.getId(), "cause", thr.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        thr.printStackTrace();
//        try {
//            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "NORMAL CLOSURE"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 指定某一端传输
     * @param action
     * @param data
     */
    final public void emit(String action, Object data){
        if (session == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);
        synchronized (session) {
            if (!session.isOpen()) {
                return;
            }
            try {
                String message = JSON.toJSONString(jsonObject,
                        SerializerFeature.WriteNonStringKeyAsString,
                        SerializerFeature.DisableCircularReferenceDetect
                );
                //session.getAsyncRemote().sendText(message);
                // java.lang.IllegalStateException: The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid state for called method
                session.getBasicRemote().sendText(message);
                Log.debug("emit session", session.getId(), "message", message);
            } catch (Exception e) {
                Log.debug("emit session", session.getId(), "error", e.getCause());
                e.printStackTrace();
            }
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
        if (no == null) {
            return;
        }
        gameCenter.removeGame(no);
    }
}
