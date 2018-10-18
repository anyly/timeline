package org.idear.timeline.websocket;

import com.alibaba.fastjson.JSONObject;

public class Player {
    private String user;
    private String img;
    private String mission;
    private JSONObject data;
    private Endpoint endpoint;

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

    public Endpoint endpoint() {
        return endpoint;
    }

    public void endpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isDisconnected() {
        return endpoint == null;
    }
}
