package com.leon.datalink.resource;


import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.constans.DriverModeEnum;

import java.util.function.Consumer;

/**
 * 驱动基础类
 */
public abstract class AbstractDriver implements Driver {

    protected DriverModeEnum driverMode;

    protected ConfigProperties properties;

    @Override
    public final void init(DriverModeEnum driverMode, ConfigProperties properties) {
        this.driverMode = driverMode;
        this.properties = properties;
    }

    @Override
    public void handleData(Object data,Consumer<Object> output) throws Exception {
        throw new UnsupportedOperationException();
    }
}
