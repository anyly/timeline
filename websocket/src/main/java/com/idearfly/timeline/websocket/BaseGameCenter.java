package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSONObject;
import com.idearfly.timeline.Film;
import com.idearfly.timeline.Projector;
import com.idearfly.utils.GenericUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * 导演,编排故事
 */
public abstract class BaseGameCenter<Game extends BaseGame> {
    private Class<Game> gameClass;
    private Projector projector;
    private LinkedHashMap<Integer, Game> allGames = new LinkedHashMap<>();
    private LinkedHashMap<String, BaseEndpoint> allEndpoints = new LinkedHashMap<>();

    public BaseGameCenter() {
        gameClass = GenericUtils.fromSuperclass(this.getClass(), BaseGame.class);
        if (gameClass == null) {
            gameClass = (Class<Game>) DefaultGame.class;
        }
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
        game.config(config);
        currentNo++;
        game.setNo(currentNo);
        if (projector == null) {
            projector = new Projector(this.getClass().getSimpleName());
        }
        game.projector(projector);
        allGames.put(currentNo, game);
        return game;
    }

    public void removeGame(int no) {
        allGames.remove(no);
    }

    public void login(BaseEndpoint endpoint) {
        // 同一个user上锁
        String user = endpoint.getUser();
        synchronized (user.intern()) {
            BaseEndpoint old = allEndpoints.get(user);
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
        LinkedHashSet<BaseEndpoint> endpoints = new LinkedHashSet<>(allEndpoints.values());
        for (BaseEndpoint endpoint: endpoints) {
            endpoint.emit(action, data);
        }
    }

    /**
     * 添加胶卷放映
     * @param film
     */
    public void addFilm(Film film) {
        projector.add(film);
    }
}
