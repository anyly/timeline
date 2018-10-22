package com.idearfly.timeline.websocket.test;

import com.alibaba.fastjson.JSONObject;
import com.idearfly.timeline.websocket.GameEndpoint;

public class MyEndpoint extends GameEndpoint {
    public JSONObject onChart(JSONObject jsonObject) {
        String chart = jsonObject.getString("chart");
        return jsonObject;
    }


}
