package com.leon.datalink.core.utils;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class ScriptUtil {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    /**
     * js脚本结果对象转为java对象
     */
    public static Object toJavaObject(Object scriptObj) {
        if (scriptObj instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) scriptObj;
            if (scriptObjectMirror.isArray()) {
                List<Object> list = Lists.newArrayList();
                for (Map.Entry<String, Object> entry : scriptObjectMirror.entrySet()) {
                    list.add(toJavaObject(entry.getValue()));
                }
                return list;
            } else {
                Map<String, Object> map = Maps.newHashMap();
                for (Map.Entry<String, Object> entry : scriptObjectMirror.entrySet()) {
                    map.put(entry.getKey(), toJavaObject(entry.getValue()));
                }
                return map;
            }
        } else if (scriptObj instanceof Double) {
            // js脚本处理后的整型和浮点都会返回浮点并且被java表示为科学计数法
            // 移除科学技术法后判断如果是整数则转为整型
            String num = decimalFormat.format(scriptObj);
            if (num.contains(".")) {
                return scriptObj;
            } else {
                return NumberUtil.toBigInteger(num);
            }
        } else {
            return scriptObj;
        }
    }

}
