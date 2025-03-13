package com.leon.datalink.web.model;


public class SqlParamVO {

    private Object data;
    private String sql;

    public Object getData() {
        return data;
    }

    public SqlParamVO setData(Object data) {
        this.data = data;
        return this;
    }

    public String getSql() {
        return sql;
    }

    public SqlParamVO setSql(String sql) {
        this.sql = sql;
        return this;
    }

}
