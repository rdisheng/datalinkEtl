package com.leon.datalink.node.script;

import java.io.Serializable;

public class Script implements Serializable {

    private String scriptId;

    private String scriptName;

    private String scriptLanguage;

    private String scriptContent;

    private String updateTime;

    public String getScriptId() {
        return scriptId;
    }

    public Script setScriptId(String scriptId) {
        this.scriptId = scriptId;
        return this;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptLanguage() {
        return scriptLanguage;
    }

    public Script setScriptLanguage(String scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
        return this;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
