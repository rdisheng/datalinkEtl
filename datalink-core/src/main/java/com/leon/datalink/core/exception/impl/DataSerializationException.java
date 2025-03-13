

package com.leon.datalink.core.exception.impl;

import com.leon.datalink.core.exception.DatalinkRuntimeException;

/**
 * datalink serialization exception.
 *
 * @author Leon
 */
public class DataSerializationException extends DatalinkRuntimeException {

    public static final int ERROR_CODE = 100;

    private static final long serialVersionUID = -4308536346316915612L;

    private static final String DEFAULT_MSG = "datalink serialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "datalink serialize for class [%s] failed. ";

    private Class<?> serializedClass;

    public DataSerializationException() {
        super(ERROR_CODE);
    }

    public DataSerializationException(Class<?> serializedClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()));
        this.serializedClass = serializedClass;
    }

    public DataSerializationException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public DataSerializationException(Class<?> serializedClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()), throwable);
        this.serializedClass = serializedClass;
    }
    
    public Class<?> getSerializedClass() {
        return serializedClass;
    }
}
