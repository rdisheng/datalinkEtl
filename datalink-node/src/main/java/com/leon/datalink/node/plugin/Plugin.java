package com.leon.datalink.node.plugin;

import java.io.Serializable;


public class Plugin implements Serializable {

    private String pluginId;

    private String pluginName;

    private String description;

    private Integer inputCount;

    private Integer outputCount;

    private String packagePath;

    private String fileName;

    private String updateTime;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getInputCount() {
        return inputCount;
    }

    public void setInputCount(Integer inputCount) {
        this.inputCount = inputCount;
    }

    public Integer getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(Integer outputCount) {
        this.outputCount = outputCount;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getFileName() {
        return fileName;
    }

    public Plugin setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
