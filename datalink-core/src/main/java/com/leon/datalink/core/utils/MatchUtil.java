package com.leon.datalink.core.utils;

import cn.hutool.core.util.StrUtil;

import java.util.List;

public class MatchUtil {

    private final String separator;

    public MatchUtil(String separator) {
        this.separator = separator;
    }

    public boolean isMatch(String id, String idFilter) {
        List<String> splitIds = StrUtil.split(id, separator);
        List<String> splitIdFilters = StrUtil.split(idFilter, separator);
        if (splitIds.size() >= splitIdFilters.size()) {
            StringBuilder newIdFilter = new StringBuilder();
            for (int i = 0; i < splitIdFilters.size(); i++) {
                String value = splitIdFilters.get(i);
                if (value.equals("+")) {
                    newIdFilter.append("+").append(separator);
                } else if (value.equals("#")) {
                    newIdFilter.append("#");
                    break;
                } else {
                    newIdFilter.append(splitIds.get(i)).append(separator);
                }
            }
            newIdFilter = new StringBuilder(StrUtil.removeSuffix(newIdFilter.toString(), separator));
            return idFilter.equals(newIdFilter.toString());
        } else return idFilter.equals(id);
    }

}
