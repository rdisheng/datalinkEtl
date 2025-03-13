package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.DriverFactory;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.constans.ResourceTypeEnum;

/**
 * 驱动型数据源节点通用actor
 */
public class DriverNodeActor extends AbstractNodeActor {

    Driver driver;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        String resourceType = properties.getString("resourceType");
        String driverMode = properties.getString("driverMode");
        if (StringUtils.isEmpty(resourceType)) throw new DataValidateException();
        driver = DriverFactory.getDriver(ResourceTypeEnum.valueOf(resourceType).getDriver());
        driver.init(StringUtils.isEmpty(driverMode) ? null : DriverModeEnum.valueOf(driverMode), properties);
        driver.create(output::out);
    }

    @Override
    public void destroy() throws Exception {
        driver.destroy();
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        driver.handleData(data, output::out);
    }

}
