package com.leon.datalink.core.exception.impl;

import com.leon.datalink.core.exception.DatalinkRuntimeException;

public class DataValidateException extends DatalinkRuntimeException {
    private static final long serialVersionUID = -2742350751684666728L;

    public static final int ERROR_CODE = 103;

    private static final String DEFAULT_MSG = "参数校验失败";

    public DataValidateException() {
        super(ERROR_CODE, DEFAULT_MSG);
    }

    public DataValidateException(String errMsg) {
        super(ERROR_CODE, errMsg);
    }

}
