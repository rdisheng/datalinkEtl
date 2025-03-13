package com.leon.datalink.runtime.entity;

import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.runtime.constants.RuntimeDataEnum;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.message.RuntimeData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RuntimeNode implements Serializable {

    private RuntimeStatusEnum status = RuntimeStatusEnum.INIT;

    private Map<RuntimeDataEnum, Long> count = new HashMap<>(RuntimeDataEnum.values().length);

    private Map<RuntimeDataEnum, LinkedList<RuntimeData>> data = new HashMap<>(RuntimeDataEnum.values().length);

    public RuntimeNode() {
        this.resetStatus();
        this.resetCountAndData();
    }

    public void addMessage(RuntimeData runtimeData) {
        RuntimeDataEnum type = runtimeData.getType();

        Long messageCount = count.get(type);
        LinkedList<RuntimeData> messageList = data.get(type);

        messageCount++;
        messageList.addFirst(runtimeData);
        if (messageList.size() > EnvUtil.getRuntimeRecordLimit()) {
            messageList.removeLast();
        }

        count.put(type, messageCount);
        data.put(type, messageList);
    }

    public void resetStatus(){
        this.status = RuntimeStatusEnum.INIT;
    }

    public void resetCountAndData() {
        for (RuntimeDataEnum runtimeDataEnum : RuntimeDataEnum.values()) {
            count.put(runtimeDataEnum, 0L);
            data.put(runtimeDataEnum, new LinkedList<>());
        }
    }

    public RuntimeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RuntimeStatusEnum status) {
        this.status = status;
    }

    public Map<RuntimeDataEnum, Long> getCount() {
        return count;
    }

    public void setCount(Map<RuntimeDataEnum, Long> count) {
        this.count = count;
    }

    public Map<RuntimeDataEnum, LinkedList<RuntimeData>> getData() {
        return data;
    }

    public void setData(Map<RuntimeDataEnum, LinkedList<RuntimeData>> data) {
        this.data = data;
    }
}
