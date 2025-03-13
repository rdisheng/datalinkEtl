package com.leon.datalink.web.model;

/**
 * @ClassNameSystemInfo
 * @Description
 * @Author Leon
 * @Date2022/4/11 10:35
 * @Version V1.0
 **/
public class SystemStatistics {

    /**
     * 资源数量
     */
    private long resourceCount;

    /**
     * 规则数量
     */
    private long ruleCount;

    public long getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(long resourceCount) {
        this.resourceCount = resourceCount;
    }

    public long getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(long ruleCount) {
        this.ruleCount = ruleCount;
    }
}
