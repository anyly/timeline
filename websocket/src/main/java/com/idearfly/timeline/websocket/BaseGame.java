package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSONObject;
import com.idearfly.timeline.Film;
import com.idearfly.timeline.Projector;
import com.idearfly.timeline.Story;
import com.idearfly.utils.GenericUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class BaseGame<Player extends BasePlayer> {
    private Class<Player> playerClass;
    protected JSONObject config;
    protected int no;
    private Projector projector;

    private Story story;

    protected BaseGame() {
        playerClass = GenericUtils.fromSuperclass(this.getClass(), BasePlayer.class);
        if (playerClass == null) {
            playerClass = (Class<Player>) BasePlayer.class;
        }

        reload();
    }

    protected LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();

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

    public JSONObject config() {
        return config;
    }

    public void config(JSONObject config) {
        this.config = config;
    }

    public void projector(Projector projector) {
        this.projector = projector;
        if (story != null) {
            this.projector.add(story);
        }
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
        ListIterator<Player> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Player player = listIterator.next();
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
        syncOthers(action, null);
    }

    /**
     * 同步所有玩家, 排除掉指定的
     * @param caller
     * @param action
     */
    final public void syncOthers(String action, Player caller) {
        LinkedList<Player> list = new LinkedList(allPlayers.values());
        ListIterator<Player> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Player player = listIterator.next();
            if (player == caller) {
                continue;
            }
            try {
                if (player != null && player.endpoint() != null) {
                    player.endpoint().emit(action, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同步所有玩家, 排除掉指定的
     * @param excludes
     * @param action
     */
    final public void syncExclude(String action, List<Player> excludes) {
        LinkedList<Player> list = new LinkedList(allPlayers.values());
        ListIterator<Player> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Player player = listIterator.next();
            if (excludes.contains(player)) {
                continue;
            }

            try {
                if (player != null && player.endpoint() != null) {
                    player.endpoint().emit(action, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加胶卷放映
     * @param film
     */
    public void addFilm(Film film) {
        if (this.projector != null) {
            projector.add(film);
        }
    }

    /**
     * 重新开始游戏
     */
    public void replay() {
        reload();
        if (this.projector != null) {
            projector.add(story);
        }
    }

    /**
     * 重新加载故事
     */
    public void reload() {
        story = story();
    }

    /**
     * 当前游戏总进度
     * @return
     */
    public String getStage() {
        return story.currentStage();
    }

}
