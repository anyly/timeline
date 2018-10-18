package org.idear.timeline.websocket;

import com.alibaba.fastjson.JSONObject;
import org.idear.timeline.Projector;
import org.idear.timeline.Story;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class Room {
    private int no;
    Projector projector;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    private LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();

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

    /////////////////广播类///////////////////////
    /**
     * 广播所有在线连接
     * @param action
     * @param data
     */
    final public void emitAll(String action, JSONObject data) {
        LinkedList<Player> list = new LinkedList(allPlayers.values());
        for (Player player: list) {
            try {
                player.endpoint().emit(action, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
