

package com.leon.datalink.core.exception.impl;


import com.leon.datalink.core.exception.DatalinkException;

/**
 * Exception to be thrown if authorization is failed.
 *
 * @author Leon
 */
public class AccessException extends DatalinkException {
    
    private static final long serialVersionUID = -2926344920552803270L;
    
    public AccessException() {
    
    }
    
    public AccessException(int code) {
        this.setErrCode(code);
    }
    
    public AccessException(String msg) {
        this.setErrMsg(msg);
    }
    
}
