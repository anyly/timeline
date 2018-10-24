package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSONObject;

public class BasePlayer {
    private String user;
    private String img;
    private String mission;
    private JSONObject data;
    private BaseEndpoint endpoint;

    public void emit(String action, Object data) {
        try {
            endpoint.emit(action, data);
        } catch (Exception e) {

        }
    }

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

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public BaseEndpoint endpoint() {
        return endpoint;
    }

    public void endpoint(BaseEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isDisconnected() {
        return endpoint == null;
    }
}
