package com.idearfly.timeline.websocket;


import com.idearfly.timeline.websocket.com.idearfly.ini.IniFormatFile;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by idear on 2018/9/21.
 */
public class ServerApplicationConfig implements javax.websocket.server.ServerApplicationConfig  {
    @Override
    final public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        Properties factory = null;
        Properties mapping = null;
        IniFormatFile iniFormatFile = IniFormatFile.classpath("websocket.ini");
        if (iniFormatFile != null) {
            factory = iniFormatFile.section("ClassFactory");
            mapping = iniFormatFile.section("URLPatternMapping");
        }

        GameCenter gameCenter = gameCenter(factory);

        Set<ServerEndpointConfig> result = new HashSet<>();
        for (Class<? extends Endpoint> cls:endpointClasses) {
            boolean isAbstract = Modifier.isAbstract(cls.getModifiers());
            if (!isAbstract) {
                String path = null;
                String[] subprotocols = null;
                Class<? extends Decoder>[] decoders = null;
                Class<? extends Encoder>[] encoders = null;
                Class<? extends ServerEndpointConfig.Configurator> configurator = null;

                ServerEndpoint serverEndpoint = cls.getAnnotation(ServerEndpoint.class);
                if (serverEndpoint != null) {
                    path = serverEndpoint.value();
                    subprotocols = serverEndpoint.subprotocols();
                    decoders = serverEndpoint.decoders();
                    encoders = serverEndpoint.encoders();
                    configurator = serverEndpoint.configurator();
                }

                String iniPath = mapping(mapping, cls);
                if (iniPath != null) {
                    path = iniPath;
                }

                ServerEndpointConfig.Builder builder = ServerEndpointConfig.Builder
                        .create(cls, path);
                if (subprotocols != null) {
                    builder.subprotocols(Arrays.asList(subprotocols));
                }
                if (decoders != null) {
                    builder.decoders(Arrays.asList(decoders));
                }
                if (encoders != null) {
                    builder.encoders(Arrays.asList(encoders));
                }
                if (configurator != null) {
                    try {
                        builder.configurator(configurator.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                ServerEndpointConfig serverEndpointConfig =  builder.build();
                serverEndpointConfig.getUserProperties().put(GameCenter.class.getName(), gameCenter);
                result.add(serverEndpointConfig);
            }
        }
        return result;
    }

    /**
     * 配置路径的映射
     * @param mapping
     * @param cls
     * @return
     */
    protected String mapping(Properties mapping, Class<? extends Endpoint> cls) {
        if (mapping != null) {
            String className = cls.getName();
            String path = mapping.getProperty(className);
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


    protected GameCenter gameCenter(Properties factory) {
        if (factory == null) {
            return new GameCenter(DefaultGame.class);
        }
        Class gameCenterClass = GameCenter.class;
        String gameCenterClassName = factory.getProperty("GameCenter");
        if (gameCenterClassName != null) {
            try {
                gameCenterClass = Class.forName(gameCenterClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Class gameClass = DefaultGame.class;
        String gameClassName = factory.getProperty("Game");
        if (gameClassName != null) {
            try {
                gameClass = Class.forName(gameClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            Constructor constructor = gameCenterClass.getConstructor(Class.class);
            return (GameCenter) constructor.newInstance(gameClass);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new GameCenter(DefaultGame.class);
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // 过滤注视类
        Set<Class<?>> annotatedEndpointClasses = new HashSet<>();
        for (Class<?> cls: scanned) {
            if (!Endpoint.class.isAssignableFrom(cls)) {
                annotatedEndpointClasses.add(cls);
            }
        }
        return annotatedEndpointClasses;
    }
}
