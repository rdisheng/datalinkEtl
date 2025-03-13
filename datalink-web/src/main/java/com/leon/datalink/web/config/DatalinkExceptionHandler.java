

package com.leon.datalink.web.config;

import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.web.model.RestResult;
import com.leon.datalink.web.model.RestResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Exception handler for web module.
 *
 * @author Leon
 */
@ControllerAdvice
public class DatalinkExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    private RestResult<Object> handleException(Exception e) {
        Loggers.WEB.error(e.getMessage());
        return RestResultUtils.failedWithMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}
