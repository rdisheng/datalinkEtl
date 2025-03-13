package com.leon.datalink.node.schedule;

import com.leon.datalink.core.serializer.ProtostuffSerializable;

public class ScheduleParam implements ProtostuffSerializable {

    private String nodeId;

    private Long initialDelay;

    private String initialDelayUnit;

    private String cronExpression;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public String getInitialDelayUnit() {
        return initialDelayUnit;
    }

    public void setInitialDelayUnit(String initialDelayUnit) {
        this.initialDelayUnit = initialDelayUnit;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
