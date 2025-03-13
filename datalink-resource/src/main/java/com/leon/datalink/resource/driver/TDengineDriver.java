package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.JdbcAbstractDriver;
import org.springframework.util.StringUtils;

public class TDengineDriver extends JdbcAbstractDriver {

    @Override
    public String url(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("databaseName"))) throw new DataValidateException();
        return String.format("jdbc:TAOS-RS://%s:%s/%s",
                properties.getString("ip"),
                properties.getString("port"),
                properties.getString("databaseName"));
    }

    @Override
    public String driverClassName() {
        return "com.taosdata.jdbc.rs.RestfulDriver";
    }

    @Override
    public String validationQuery() {
        return "select server_status();";
    }

}
