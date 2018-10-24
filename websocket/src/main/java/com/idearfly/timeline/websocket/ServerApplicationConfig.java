package com.idearfly.timeline.websocket;



import com.idearfly.ini.IniFormatFile;
import com.idearfly.utils.GenericUtils;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Created by idear on 2018/9/21.
 */
public class ServerApplicationConfig implements javax.websocket.server.ServerApplicationConfig  {

    @Override
    final public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        Map<Class<BaseEndpoint>, BaseGameCenter> allGameCenters = new HashMap<>();

        Properties factory = null;
        Properties mapping = null;
        IniFormatFile iniFormatFile = IniFormatFile.classpath("websocket.ini");
        if (iniFormatFile != null) {
            factory = iniFormatFile.section("ClassFactory");
            mapping = iniFormatFile.section("URLPatternMapping");
        }

        Set<ServerEndpointConfig> result = new HashSet<>();
        for (Class<? extends Endpoint> cls:endpointClasses) {
            boolean isAbstract = Modifier.isAbstract(cls.getModifiers());
            if (!isAbstract) {

                ServerEndpoint serverEndpoint = cls.getAnnotation(ServerEndpoint.class);
                if (serverEndpoint == null) {
                    continue;
                }

                String path = serverEndpoint.value();
                String[] subprotocols = serverEndpoint.subprotocols();
                Class<? extends Decoder>[] decoders = serverEndpoint.decoders();
                Class<? extends Encoder>[] encoders = serverEndpoint.encoders();
                Class<? extends ServerEndpointConfig.Configurator> configurator = serverEndpoint.configurator();

                String iniPath = mapping(mapping, cls);
                if (iniPath != null) {
                    path = iniPath;
                }

                if (path == null) {
                    path = pathFromClass(cls);
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

                //
                if (BaseEndpoint.class.isAssignableFrom(cls)) {
                    Class<BaseEndpoint> endpointClass = (Class<BaseEndpoint>) cls;
                    BaseGameCenter gameCenter = allGameCenters.get(endpointClass);
                    if (gameCenter == null) {
                        gameCenter = gameCenter(endpointClass);
                        allGameCenters.put(endpointClass, gameCenter);
                    }
                    String endpointName = endpointClass.getName();
                    serverEndpointConfig.getUserProperties().put(endpointName, gameCenter);
                }

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
    private String mapping(Properties mapping, Class<? extends Endpoint> cls) {
        if (mapping != null) {
            String className = cls.getName();
            String path = mapping.getProperty(className);
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    private String pathFromClass(Class<? extends Endpoint> cls) {
        // 默认按照类名路径
        String path = cls.getSimpleName();
        if(Character.isUpperCase(path.charAt(0))) {
            path = (new StringBuilder()).append(Character.toLowerCase(path.charAt(0))).append(path.substring(1)).toString();
        }
        return "/"+path;
    }

    private BaseGameCenter gameCenter(Class<? extends BaseEndpoint> cls) {
        try {
            Class<? extends BaseGameCenter> gameCenterClass = GenericUtils.fromSuperclass(cls, BaseGameCenter.class);
            return gameCenterClass.newInstance();
        } catch (Exception e) {

        }
        return new DefaultGameCenter();
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
