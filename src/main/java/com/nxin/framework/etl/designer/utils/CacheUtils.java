package com.nxin.framework.etl.designer.utils;

import java.util.concurrent.ConcurrentHashMap;

public class CacheUtils {
    private static int MAX = 1024 * 1024;
    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>(MAX);

    public static String get(String key) {
        return CACHE.get(key);
    }

    public static String cache(String key, String value) {
        CACHE.put(key, value);
        return CACHE.get(key);
    }

    public static void remove(String key) {
        CACHE.remove(key);
    }
}
