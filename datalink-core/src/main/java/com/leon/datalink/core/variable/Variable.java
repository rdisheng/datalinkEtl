package com.leon.datalink.core.variable;

import com.leon.datalink.core.serializer.ProtostuffSerializable;

import java.util.Objects;

public class Variable  implements ProtostuffSerializable {

    private String key;

    private Object value;

    private String desc;

    private VariableTypeEnum type;

    private String memberName;

    public Variable() {
    }
    public Variable(String key, Object value, String desc, VariableTypeEnum type) {
        this.key = key;
        this.value = value;
        this.desc = desc;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VariableTypeEnum getType() {
        return type;
    }

    public void setType(VariableTypeEnum type) {
        this.type = type;
    }

    public String getMemberName() {
        return memberName;
    }

    public Variable setMemberName(String memberName) {
        this.memberName = memberName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return key.equals(variable.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
