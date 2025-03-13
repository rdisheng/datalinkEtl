package com.leon.datalink.cluster.distributed;

import com.leon.datalink.core.serializer.ProtostuffSerializable;

/**
 * 数据同步
 */
public class ConsistencyWrapper implements ProtostuffSerializable {

    private Class<?> clazz;

    private ConsistencyCommandEnum command;

    private Object data;

    public static ConsistencyWrapper init(Class<?> clazz) {
        return new ConsistencyWrapper().setClazz(clazz).setCommand(ConsistencyCommandEnum.INIT);
    }

    public static ConsistencyWrapper add(Class<?> clazz, Object data) {
        return new ConsistencyWrapper().setClazz(clazz).setCommand(ConsistencyCommandEnum.ADD).setData(data);
    }

    public static ConsistencyWrapper delete(Class<?> clazz, Object data) {
        return new ConsistencyWrapper().setClazz(clazz).setCommand(ConsistencyCommandEnum.DELETE).setData(data);
    }

    public static ConsistencyWrapper destroy(Class<?> clazz) {
        return new ConsistencyWrapper().setClazz(clazz).setCommand(ConsistencyCommandEnum.DESTROY);
    }

    public ConsistencyWrapper setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public ConsistencyWrapper setCommand(ConsistencyCommandEnum command) {
        this.command = command;
        return this;
    }

    public ConsistencyWrapper setData(Object data) {
        this.data = data;
        return this;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public ConsistencyCommandEnum getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }
}
