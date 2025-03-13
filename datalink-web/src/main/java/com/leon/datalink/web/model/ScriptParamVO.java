package com.leon.datalink.web.model;


public class ScriptParamVO {

    private String language;
    private String param;
    private String script;

    public String getLanguage() {
        return language;
    }

    public ScriptParamVO setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getParam() {
        return param;
    }

    public ScriptParamVO setParam(String param) {
        this.param = param;
        return this;
    }

    public String getScript() {
        return script;
    }

    public ScriptParamVO setScript(String script) {
        this.script = script;
        return this;
    }
}
