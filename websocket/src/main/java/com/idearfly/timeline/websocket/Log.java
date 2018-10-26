package com.idearfly.timeline.websocket;

import com.alibaba.fastjson.JSON;

public class Log {

    public static void debug(Object...pieces) {
        debug(null, pieces);
    }

    public static void debug(Class cls, Object...pieces) {
        StringBuffer stringBuffer = log(cls, pieces);
        System.out.println(stringBuffer.toString());
    }

    public static void error(Object...pieces) {
        error(null, pieces);
    }

    public static void error(Class cls, Object...pieces) {
        StringBuffer stringBuffer = log(cls, pieces);
        System.err.println(stringBuffer.toString());
    }

    private static StringBuffer log(Class cls, Object...pieces) {
        if (cls == null) {
            cls = Log.class;
        }
        String packageName = cls.getPackage().getName();
        StringBuffer stringBuffer = new StringBuffer("["+packageName+"]");
        for (Object piece : pieces) {
            stringBuffer.append(" > ");
            if (piece == null) {
                stringBuffer.append("null");
                continue;
            } else if (piece.getClass() == String.class) {
                stringBuffer.append(piece);
                continue;
            }
            try {
                String s = JSON.toJSONString(piece);
                stringBuffer.append(s);
            } catch (Exception e) {
                stringBuffer.append(piece);
            }
        }
        return stringBuffer;
    }
}
