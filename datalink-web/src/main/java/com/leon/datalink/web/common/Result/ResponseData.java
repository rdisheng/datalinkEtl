package com.leon.datalink.web.common.Result;

public class ResponseData {
    public static <T> Result<T> success(T info) {
        return new Result<T>().setCode(ResultsCode.SUCCESS).setMsg(ResultsCode.SUCCESS.message).setData(info);
    }

    public static Result success() {
        return new Result();
    }

    public static Result error() {
        return error(ResultsCode.FAIL.message);
    }

    public static Result error(String message) {
        return error(ResultsCode.FAIL, message);
    }

    public static Result error(ResultsCode resultsCode, String message) {
        return new Result().setCode(resultsCode.code).setMsg(message);
    }

    public static Result authError() {
        return error(ResultsCode.AUTH_ERROR, ResultsCode.AUTH_ERROR.message);
    }

    public static Result forbidden() {
        return error(ResultsCode.FORBIDDEN, ResultsCode.FORBIDDEN.message);
    }


}