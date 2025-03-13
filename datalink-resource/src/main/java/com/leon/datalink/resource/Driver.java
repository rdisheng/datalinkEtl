package com.leon.datalink.resource;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.constans.DriverModeEnum;

import java.util.function.Consumer;

/**
 * @ClassName Driver
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:10
 * @Version V1.0
 **/
public interface Driver {

    /**
     * init driver
     */
    void init(DriverModeEnum driverMode, ConfigProperties properties);

    /**
     * create driver
     */
    void create(Consumer<Object> consumer) throws Exception;

    /**
     * destroy client
     */
    void destroy() throws Exception;

    /**
     * test ok
     */
    boolean test(ConfigProperties properties);

    /**
     * handle receive data
     */
    void handleData(Object data,Consumer<Object> consumer) throws Exception;


}
