package org.idear.timeline.websocket.test;

import com.alibaba.fastjson.JSONObject;
import org.idear.timeline.websocket.GameEndpoint;

public class MyEndpoint extends GameEndpoint {
    public JSONObject onChart(JSONObject jsonObject) {
        String chart = jsonObject.getString("chart");
        return jsonObject;
    }


}
