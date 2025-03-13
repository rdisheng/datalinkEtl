package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.JdbcAbstractDriver;
import org.springframework.util.StringUtils;

public class PostgresqlDriver extends JdbcAbstractDriver {

    @Override
    public String url(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("databaseName"))) throw new DataValidateException();
        return String.format("jdbc:postgresql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT",
                properties.getString("ip"),
                properties.getString("port"),
                properties.getString("databaseName"));
    }

    @Override
    public String driverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String validationQuery() {
        return "select version();";
    }

}
