package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.JdbcAbstractDriver;
import org.springframework.util.StringUtils;

public class SqlServerDriver extends JdbcAbstractDriver {

    @Override
    public String url(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("databaseName"))) throw new DataValidateException();
        return String.format("jdbc:sqlserver://%s:%s;DatabaseName=%s",
                properties.getString("ip"),
                properties.getString("port"),
                properties.getString("databaseName"));
    }

    @Override
    public String driverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String validationQuery() {
        return "select 1;";
    }

}
