package com.leon.datalink.core.variable;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.VersionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 全局变量
 */
public class GlobalVariableContent {

    private static final ConcurrentHashMap<String, Variable> globalVariable = new ConcurrentHashMap<>();

    static {
        globalVariable.put("localMemberName", new Variable("localMemberName", null, "本地节点", VariableTypeEnum.SYSTEM));
        globalVariable.put("version", new Variable("version", VersionUtils.version, "当前系统版本", VariableTypeEnum.SYSTEM));
        globalVariable.put("timestamp", new Variable("timestamp", null, "当前毫秒时间戳", VariableTypeEnum.SYSTEM));
        globalVariable.put("date", new Variable("date", null, "当前日期", VariableTypeEnum.SYSTEM));
        globalVariable.put("dateTime", new Variable("dateTime", null, "当前日期时间", VariableTypeEnum.SYSTEM));
        globalVariable.put("uuid", new Variable("uuid", null, "UUID(动态)", VariableTypeEnum.SYSTEM));
        globalVariable.put("snowId", new Variable("snowId", null, "雪花算法ID(动态)", VariableTypeEnum.SYSTEM));
    }

    private static void freshSystemVariable() {
        DateTime now = DateTime.now();
        setValue("timestamp", now.getTime());
        setValue("date", DateUtil.format(now, "yyyy-MM-dd"));
        setValue("dateTime", DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
        setValue("uuid", UUID.randomUUID().toString());
        setValue("snowId", SnowflakeIdWorker.getId());
    }

    public static Variable get(String key) {
        return globalVariable.get(key);
    }

    public static void set(String key, Variable variable) {
        globalVariable.put(key, variable);
    }

    public static Object getValue(String key) {
        return get(key).getValue();
    }

    public static void setValue(String key, Object value) {
        globalVariable.get(key).setValue(value);
    }

    public static Map<String, Variable> getAll() {
        freshSystemVariable();
        return globalVariable;
    }

    public static Map<String, Object> getAllValue() {
        Map<String, Object> result = new HashMap<>();
        for (String key : getAll().keySet()) {
            result.put(key, getValue(key));
        }
        return result;
    }

    public static Map<String, Object> getAllValueWith(Object data) {
        Map<String, Object> globalVar = getAllValue();
        if (JacksonUtils.canToMap(data)) {
            globalVar.putAll(JacksonUtils.convertValue(data, new TypeReference<Map<String, Object>>() {
            }));
        }
        return globalVar;
    }

    public static void remove(String key) {
        globalVariable.remove(key);
    }

    public static synchronized List<Variable> getCustomVariables() {
        return globalVariable.values().stream().filter(variable -> variable.getType().equals(VariableTypeEnum.CUSTOM)).collect(Collectors.toList());
    }

    public static synchronized List<Variable> getMemberCustomVariables(String memberName) {
        return globalVariable.values().stream().filter(variable ->
                variable.getType().equals(VariableTypeEnum.CUSTOM) && memberName.equals(variable.getMemberName())
        ).collect(Collectors.toList());
    }

    public static synchronized void setAllCustomVariables(Set<Variable> variables) {
//        Collection<Variable> values = globalVariable.values();
//        if (values.size() == variables.size() && values.containsAll(variables)) return;
        globalVariable.entrySet().removeIf(entry -> entry.getValue().getType().equals(VariableTypeEnum.CUSTOM));
        for (Variable variable : variables) {
            set(variable.getKey(), variable);
        }
    }


}
