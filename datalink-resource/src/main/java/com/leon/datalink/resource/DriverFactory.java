
package com.leon.datalink.resource;

import java.lang.reflect.Constructor;


/**
 * DriverFactory
 * @author leon
 */
public class DriverFactory {

    public static Driver getDriver(Class<? extends Driver> driverClass) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor();
        return constructor.newInstance();
    }

}
