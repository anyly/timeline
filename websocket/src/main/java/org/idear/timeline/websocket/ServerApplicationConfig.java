package org.idear.timeline.websocket;


import org.idear.timeline.websocket.test.MyGame;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by idear on 2018/9/21.
 */
public class ServerApplicationConfig implements javax.websocket.server.ServerApplicationConfig  {
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        GameCenter gameCenter = gameCenter();

        Set<ServerEndpointConfig> result = new HashSet<>();
        for (Class<? extends Endpoint> cls:endpointClasses) {
            boolean isAbstract = Modifier.isAbstract(cls.getModifiers());
            if (!isAbstract) {
                String path = cls.getSimpleName();
                if(Character.isUpperCase(path.charAt(0))) {
                    path = (new StringBuilder()).append(Character.toLowerCase(path.charAt(0))).append(path.substring(1)).toString();
                }
                ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(cls, "/"+path).build();
                serverEndpointConfig.getUserProperties().put(GameCenter.class.getName(), gameCenter);
                result.add(serverEndpointConfig);
            }
        }
        return result;
    }

    public GameCenter gameCenter() {
        return new GameCenter(MyGame.class);
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // 过滤注视类
        return scanned;
    }
}
