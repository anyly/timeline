package org.idear.timeline.websocket;

import com.alibaba.fastjson.JSON;

public class Log {

    public static void debug(Object...pieces) {
        debug(null, pieces);
    }

    public static void debug(Class cls, Object...pieces) {
        if (cls == null) {
            cls = Log.class;
        }
        String packageName = cls.getPackage().getName();
        StringBuffer stringBuffer = new StringBuffer("["+packageName+"]");
        for (Object piece : pieces) {
            stringBuffer.append(" > ");
            try {
                String s = JSON.toJSONString(piece);
                stringBuffer.append(s);
            } catch (Exception e) {
                stringBuffer.append(piece);
            }

        }
        System.out.println(stringBuffer.toString());
    }

    public static void error(Object...pieces) {
        error(null, pieces);
    }

    public static void error(Class cls, Object...pieces) {
        if (cls == null) {
            cls = Log.class;
        }
        String packageName = cls.getPackage().getName();
        StringBuffer stringBuffer = new StringBuffer("["+packageName+"]");
        for (Object piece : pieces) {
            stringBuffer.append(" > ");
            try {
                String s = JSON.toJSONString(piece);
                stringBuffer.append(s);
            } catch (Exception e) {
                stringBuffer.append(piece);
            }
        }
        System.err.println(stringBuffer.toString());
    }
}
