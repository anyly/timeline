package org.idear.timeline.websocket;

import com.alibaba.fastjson.JSONObject;
import org.idear.timeline.Projector;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * 导演,编排故事
 */
public class GameCenter {
    private Class<? extends Game> gameClass;
    private Projector projector = new Projector();
    private LinkedHashMap<Integer, Game> allGames = new LinkedHashMap<>();
    private LinkedHashMap<String, Endpoint> allEndpoints = new LinkedHashMap<>();

    public GameCenter(Class<? extends Game> gameClass) {
        this.gameClass = gameClass;
    }

    private int currentNo = 1000;

    public Game game(int no) {
        return allGames.get(no);
    }

    //////////getter setter///////////

    ////////////业务///////////////
    public synchronized Game newGame(JSONObject config) {
        Game game = null;
        try {
            game = gameClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        game.setConfig(config);
        currentNo++;
        game.setNo(currentNo);
        game.projector = projector;
        allGames.put(currentNo, game);
        return game;
    }

    public void removeGame(int no) {
        allGames.remove(no);
    }

    public void login(Endpoint endpoint) {
        // 同一个user上锁
        String user = endpoint.getUser();
        synchronized (user.intern()) {
            Endpoint old = allEndpoints.get(user);
            if (old != null) {
                try {
                    old.session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            allEndpoints.put(user, endpoint);
        }
    }

    public void logout(String user) {
        allEndpoints.remove(user);
    }

    public void emitAll(String action, Object data) {
        LinkedHashSet<Endpoint> endpoints = new LinkedHashSet<>(allEndpoints.values());
        for (Endpoint endpoint: endpoints) {
            endpoint.emit(action, data);
        }
    }
}
