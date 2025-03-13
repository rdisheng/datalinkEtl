package com.leon.datalink.runtime.message;

import cn.hutool.core.date.DateUtil;
import com.leon.datalink.cluster.member.ClusterMemberManager;
import com.leon.datalink.core.serializer.ProtostuffSerializable;
import com.leon.datalink.runtime.constants.RuntimeDataEnum;

public class RuntimeData implements ProtostuffSerializable {

    private RuntimeDataEnum type;

    private String nodeId;

    private Integer port;

    private Object data;

    private String time = DateUtil.now();

    private String member = ClusterMemberManager.getLocalMemberName();

    public static RuntimeData input(String nodeId, Object data) {
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setType(RuntimeDataEnum.INPUT);
        runtimeData.setNodeId(nodeId);
        runtimeData.setData(data);
        return runtimeData;
    }

    public static RuntimeData output(String nodeId, Integer port, Object data) {
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setType(RuntimeDataEnum.OUTPUT);
        runtimeData.setNodeId(nodeId);
        runtimeData.setPort(port);
        runtimeData.setData(data);
        return runtimeData;
    }

    public static RuntimeData exception(String nodeId, Object data) {
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setType(RuntimeDataEnum.EXCEPTION);
        runtimeData.setNodeId(nodeId);
        runtimeData.setData(data);
        return runtimeData;
    }

    public RuntimeDataEnum getType() {
        return type;
    }

    public void setType(RuntimeDataEnum type) {
        this.type = type;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
