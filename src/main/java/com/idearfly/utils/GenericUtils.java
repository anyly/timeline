package com.idearfly.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericUtils {

    public static Class fromSuperclass(Class cls, Class lowBound) {
        do {
            Type type = cls.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Class cl = fromSuperclass(pType, lowBound);
                if (cl != null) {
                    return cl;
                }
            }
            cls = cls.getSuperclass();
        } while (cls != Object.class);
        return null;
    }

    private static Class actualTypeArguments(Type type, Class lowBound) {
        if (type instanceof Class) {
            Class cls = (Class) type;
            if (lowBound.isAssignableFrom(cls)) {
                return cls;
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class cls = fromSuperclass(pType, lowBound);
            if (cls != null) {
                return cls;
            }
        }
        return null;
    }

    public static Class fromSuperclass(ParameterizedType parameterizedType, Class lowBound) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        for (Type type:typeArguments) {
            Class cl = actualTypeArguments(type, lowBound);
            if (cl != null) {
                return cl;
            }
        }
        return null;
    }
}
