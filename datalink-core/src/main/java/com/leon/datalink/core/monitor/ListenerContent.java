package com.leon.datalink.core.monitor;

import java.util.*;

/**
 * 监听器
 */
public class ListenerContent {

    private static final Map<Integer, Listener> listeners = new HashMap<>();

    public static void add(int port, ListenerTypeEnum type, String desc) {
        add("0.0.0.0", port, type, desc);
    }

    public static void add(String ip, int port, ListenerTypeEnum type, String desc) {
        listeners.put(port, new Listener(ip, port, type, desc));
    }

    public static List<Listener> getList() {
        Collection<Listener> values = listeners.values();
        return new ArrayList<>(values);
    }

    public static void remove(int port) {
        listeners.remove(port);
    }


}
