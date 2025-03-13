package com.leon.datalink.web.common.Result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApiModel
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<String, Object> mapData = new HashMap<String, Object>();

    @ApiModelProperty(value = "状态码")
    public int code;

    @ApiModelProperty(value = "信息")
    private String msg;

    @ApiModelProperty(value = "对象")
    private T data;

    public Result() {
        this.code = ResultsCode.SUCCESS.code;
        this.msg = ResultsCode.SUCCESS.message;
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(ResultsCode resultsCode) {
        this.code = resultsCode.code;
        return this;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Result<T> putDataValue(String key, Object value) {
        mapData.put(key, value);
        this.data = (T) mapData;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", info=" + data +
                '}';
    }
}