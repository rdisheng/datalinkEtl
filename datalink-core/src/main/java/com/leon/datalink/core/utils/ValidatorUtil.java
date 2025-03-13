package com.leon.datalink.core.utils;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.leon.datalink.core.exception.impl.DataValidateException;

/**
 * field validator util
 * @author leon
 */
public class ValidatorUtil {

    public static void isNotNull(Object... values) {
        for (Object value : values) {
            isNotNull(value);
        }
    }

    public static void isNotNull(Object value) {
        if (null == value) {
            throw new DataValidateException();
        }
    }

    public static void isNotEmpty(Object... values) {
        for (Object value : values) {
            isNotEmpty(value);
        }
    }

    public static void isNotEmpty(Object value) {
        isNotNull(value);
        if (value instanceof String && StrUtil.isEmpty((String) value)) {
            throw new DataValidateException();
        }
    }

    public static void isNumber(CharSequence... values) {
        for (CharSequence value : values) {
            isNumber(value);
        }
    }

    public static void isNumber(CharSequence value) {
        isNotEmpty(value);
        if (!NumberUtil.isNumber(value)) {
            throw new DataValidateException();
        }
    }

}
