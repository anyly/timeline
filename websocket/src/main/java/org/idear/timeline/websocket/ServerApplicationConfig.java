package org.idear.timeline.websocket;


import org.idear.timeline.websocket.test.MyGame;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by idear on 2018/9/21.
 */
public class ServerApplicationConfig implements javax.websocket.server.ServerApplicationConfig  {
    @Override
    final public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        GameCenter gameCenter = gameCenter();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("websocket-mapping");

        Set<ServerEndpointConfig> result = new HashSet<>();
        for (Class<? extends Endpoint> cls:endpointClasses) {
            boolean isAbstract = Modifier.isAbstract(cls.getModifiers());
            if (!isAbstract) {
                String path = mapping(resourceBundle, cls);
                ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(cls, path).build();
                serverEndpointConfig.getUserProperties().put(GameCenter.class.getName(), gameCenter);
                result.add(serverEndpointConfig);
            }
        }
        return result;
    }

    protected String mapping(ResourceBundle resourceBundle, Class<? extends Endpoint> cls) {
        if (resourceBundle != null) {
            String className = cls.getName();
            String path = resourceBundle.getString(className);
            if (path != null) {
                return path;
            }
        }
        // 默认按照类名路径
        String path = cls.getSimpleName();
        if(Character.isUpperCase(path.charAt(0))) {
            path = (new StringBuilder()).append(Character.toLowerCase(path.charAt(0))).append(path.substring(1)).toString();
        }
        return "/"+path;
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
