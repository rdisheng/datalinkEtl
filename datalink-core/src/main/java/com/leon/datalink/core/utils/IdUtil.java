package com.leon.datalink.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 生成随机ID
 */
public class IdUtil {

    private static final List<String> charList = CollUtil.newArrayList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

    public static String getId(int length) {
        return StringUtils.join(RandomUtil.randomEleSet(charList, length), "");
    }

    public static String getId(int length, List<String> excludeList) {
        String id;
        do {
            id = getId(length);
        } while (CollectionUtil.isNotEmpty(excludeList) && excludeList.contains(id));
        return id;
    }

}
