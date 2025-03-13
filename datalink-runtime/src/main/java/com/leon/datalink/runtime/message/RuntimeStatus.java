package com.leon.datalink.runtime.message;

import com.leon.datalink.core.serializer.ProtostuffSerializable;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;

public class RuntimeStatus implements ProtostuffSerializable {

    private String nodeId;

    private RuntimeStatusEnum status;

    public static RuntimeStatus init(String nodeId) {
        RuntimeStatus runtimeStatus = new RuntimeStatus();
        runtimeStatus.setNodeId(nodeId);
        runtimeStatus.setStatus(RuntimeStatusEnum.INIT);
        return runtimeStatus;
    }

    public static RuntimeStatus normal(String nodeId) {
        RuntimeStatus runtimeStatus = new RuntimeStatus();
        runtimeStatus.setNodeId(nodeId);
        runtimeStatus.setStatus(RuntimeStatusEnum.NORMAL);
        return runtimeStatus;
    }

    public static RuntimeStatus abnormal(String nodeId) {
        RuntimeStatus runtimeStatus = new RuntimeStatus();
        runtimeStatus.setNodeId(nodeId);
        runtimeStatus.setStatus(RuntimeStatusEnum.ABNORMAL);
        return runtimeStatus;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public RuntimeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RuntimeStatusEnum status) {
        this.status = status;
    }

}
