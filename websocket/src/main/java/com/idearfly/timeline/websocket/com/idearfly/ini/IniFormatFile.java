package com.idearfly.timeline.websocket.com.idearfly.ini;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;

public class IniFormatFile {
    private LinkedHashMap<String, Properties> sections = new LinkedHashMap<>();

    private IniFormatFile() {}

    public static IniFormatFile classpath(String path) {
        URL url = IniFormatFile.class.getClassLoader().getResource(path);
        if (url == null) {
            return null;
        }
        return load(url.getPath());
    }

    public static IniFormatFile load(String path) {
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        IniFormatFile iniFormatFile = new IniFormatFile();
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputReader = new InputStreamReader(new FileInputStream(file));
            bufferedReader = new BufferedReader(inputReader);
            // 按行读取字符串
            String line;
            String section = "";
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                } else if (line.startsWith(";")) {
                    continue;
                } else if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length()-1).trim();
                } else {
                    String[] split = line.split("=");
                    Properties properties = iniFormatFile.sections.get(section);
                    if (properties == null) {
                        properties = new Properties();
                        iniFormatFile.sections.put(section, properties);
                    }
                    if (split.length == 1) {
                        properties.setProperty(split[0].trim(), "");
                    } else {
                        properties.setProperty(split[0].trim(), split[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return iniFormatFile;
    }

    public Properties section(String section) {
        return sections.get(section);
    }
}
