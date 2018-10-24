package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSONObject;
import com.idearfly.timeline.Projector;
import com.idearfly.timeline.Story;
import com.idearfly.utils.GenericUtils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class BaseGame<Player extends BasePlayer> {
    private Class<Player> playerClass;
    private JSONObject config;
    private int no;
    Projector projector;

    protected BaseGame() {
        playerClass = GenericUtils.fromSuperclass(this.getClass(), BasePlayer.class);
        if (playerClass == null) {
            playerClass = (Class<Player>) BasePlayer.class;
        }
    }

    private LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();

    //////////////////getter setter ////////////////////////
    public Class<Player> playerClass() {
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
    public synchronized void join(Player player) {
        allPlayers.put(player.getUser(), player);
    }

    /**
     * 离开剧情
     * @param player
     */
    public synchronized void leave(Player player) {
        allPlayers.remove(player);
    }

    public Player player(String user) {
        return allPlayers.get(user);
    }

    //[广播类]
    /**
     * 广播所有在线连接
     * @param action
     * @param data
     */
    final public void emitAll(String action, JSONObject data) {
        emitOthers(null, action, data);
    }

    /**
     * 广播除了自己以外的其他玩家
     * @param caller
     * @param action
     * @param data
     */
    final public void emitOthers(Player caller, String action, JSONObject data) {
        LinkedList<Player> list = new LinkedList(allPlayers.values());
        for (Player player: list) {
            if (player == caller) {
                continue;
            }
            try {
                player.endpoint().emit(action, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //[同步数据]
    /**
     * 同步所有人
     * @param action
     */
    final public void syncAll(String action) {
        syncOthers(null, action);
    }

    /**
     * 同步除了自己以外的其他玩家
     * @param caller
     * @param action
     */
    final public void syncOthers(BasePlayer caller, String action) {
        LinkedList<Player> list = new LinkedList(allPlayers.values());
        for (Player player: list) {
            if (player == caller) {
                continue;
            }
            try {
                player.endpoint().emit(action, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
