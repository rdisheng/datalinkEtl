package com.leon.datalink.rule.entity;


import java.io.Serializable;

public class Rule implements Serializable {

    private String ruleId;

    private String ruleName;

    private Integer nodeCount;

    private String updateTime;

    private String graphJson;

    private Boolean enable;

    public String getRuleId() {
        return ruleId;
    }

    public Rule setRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getGraphJson() {
        return graphJson;
    }

    public Rule setGraphJson(String graphJson) {
        this.graphJson = graphJson;
        return this;
    }

    public Boolean isEnable() {
        return enable;
    }

    public Rule setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }
}
