package org.idear.timeline.websocket;

import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class Endpoint extends javax.websocket.Endpoint {
    private Session session;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.session.addMessageHandler((MessageHandler.Whole<String>) partialMessage -> {

        });
    }
}
