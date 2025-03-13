package com.leon.datalink.node.graph;

import com.leon.datalink.core.config.ConfigProperties;

import java.util.Map;

public class NodeData {

    private String name;

    private String code;

    private ConfigProperties properties;

    private Map<String,Integer> ports;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ConfigProperties getProperties() {
        return properties;
    }

    public void setProperties(ConfigProperties properties) {
        this.properties = properties;
    }

    public Map<String, Integer> getPorts() {
        return ports;
    }

    public void setPorts(Map<String, Integer> ports) {
        this.ports = ports;
    }
}
