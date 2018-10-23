package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSONObject;
import com.idearfly.timeline.Projector;
import com.idearfly.timeline.Story;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class Game<T extends Player> {
    private Class<T> playerClass = (Class<T>) Player.class;
    private JSONObject config;
    private int no;
    Projector projector;

    protected Game() {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
            playerClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        } catch (Exception e) {

        }
    }

    private LinkedHashMap<String, T> allPlayers = new LinkedHashMap<>();

    //////////////////getter setter ////////////////////////
    public Class<T> playerClass() {
        return playerClass;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    ////////////////故事编排///////////////
    public abstract Story story();

    ////////////////账号登录/登出相关////////////////////////////
    /**
     * 加入剧情
     * @param player
     */
    public synchronized void join(T player) {
        allPlayers.put(player.getUser(), player);
    }

    /**
     * 离开剧情
     * @param player
     */
    public synchronized void leave(T player) {
        allPlayers.remove(player);
    }

    public T player(String user) {
        return allPlayers.get(user);
    }

    /////////////////广播类///////////////////////
    /**
     * 广播所有在线连接
     * @param action
     * @param data
     */
    final public void emitAll(String action, JSONObject data) {
        LinkedList<T> list = new LinkedList(allPlayers.values());
        for (T player: list) {
            try {
                player.endpoint().emit(action, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
