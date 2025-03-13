package com.leon.datalink.core.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.JacksonUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置参数
 */
public class ConfigProperties extends LinkedHashMap<String, Object> {

    public String getString(String key) {
        Object o = this.get(key);
        if (o instanceof String) {
            return (String) o;
        } else {
            return JacksonUtils.convertValue(o, String.class);
        }
    }

    public String getString(String key, String defaultValue) {
        String strProp = getString(key);
        if (null == strProp) return defaultValue;
        else return strProp;
    }

    public Integer getInteger(String key) {
        Object o = this.get(key);
        if (null == o) return null;
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            return JacksonUtils.convertValue(o, Integer.class);
        }
    }

    public Integer getInteger(String key, int defaultValue) {
        Integer intProp = getInteger(key);
        if (null == intProp) return defaultValue;
        else return intProp;
    }

    public Long getLong(String key) {
        Object o = this.get(key);
        if (null == o) return null;
        if (o instanceof Long) {
            return (Long) o;
        } else {
            return JacksonUtils.convertValue(o, Long.class);
        }
    }

    public Long getLong(String key, long defaultValue) {
        Long longProp = getLong(key);
        if (null == longProp) return defaultValue;
        else return longProp;
    }

    public Boolean getBoolean(String key) {
        Object o = this.get(key);
        if (null == o) return null;
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return JacksonUtils.convertValue(o, Boolean.class);
        }
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        Boolean boolProp = getBoolean(key);
        if (null == boolProp) return defaultValue;
        else return boolProp;
    }

    public Map<String, String> getMap(String key) {
        Object o = this.get(key);
        if (null == o) return null;
        return JacksonUtils.convertValue(o, new TypeReference<Map<String, String>>() {
        });
    }

    public List<Map<String, Object>> getList(String key) {
        Object o = this.get(key);
        if (null == o) return null;
        return JacksonUtils.convertValue(o, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    public List<String> getStringList(String key) {
        Object o = this.get(key);
        if (null == o) return null;
        return JacksonUtils.convertValue(o, new TypeReference<List<String>>() {
        });
    }


    public <T> T getObject(String key,Class<T> tClass) {
        Object o = this.get(key);
        if (null == o) return null;
        return JacksonUtils.convertValue(o, tClass);
    }

}
