package com.leon.datalink.node.response;

import java.util.concurrent.ConcurrentHashMap;

public class ResponseManager {

    //todo 定时过期
    private static final ConcurrentHashMap<String, Object> responseMap = new ConcurrentHashMap<>();

    public static void add(String requestId, Object responseHandler) {
        responseMap.put(requestId, responseHandler);
    }

    public static Object get(String requestId) {
        Object result = responseMap.get(requestId);
        if (result != null) {
            responseMap.remove(requestId);
        }
        return result;
    }


}
