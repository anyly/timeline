package com.idearfly.timeline.websocket;

import com.idearfly.timeline.websocket.annotation.RequireLogin;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public abstract class GameEndpoint<GameCenter extends BaseGameCenter<Game>,
        Game extends BaseGame<Player>, Player extends BasePlayer> extends BaseEndpoint<GameCenter> {
    protected Game game;
    protected Player player;

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        if (player != null) {
            player.endpoint(null);
            player = null;
        }

    }

    @RequireLogin
    public Game onJoinGame(Integer no) {
        if (no == null) {
            return null;
        }
        game = gameCenter.game(no);
        if (game == null) {
            return null;
        }
        player = game.player(user);
        if (player != null) {
            // 已经在游戏中,挤下线
            try {
                player.endpoint().session.close();
            } catch (Exception e) {

            }
            player.endpoint(null);
        } else {
            // 新进入游戏
            try {
                player = game.playerClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            player.setUser(user);
            player.setImg(img);
            game.join(player);
        }
        // 允许更换头像
        player.setImg(img);
        player.endpoint(this);

        return game;

    }


    public Integer onLeaveGame() {
        if (game == null) {
            return null;
        }
        if (player != null) {
            game.leave(player);
            player.endpoint(null);
        }
        return game.getNo();
    }
}
