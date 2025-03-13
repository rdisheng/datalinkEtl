package com.leon.datalink.resource;

import com.leon.datalink.core.exception.impl.DataValidateException;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * JDBC驱动基础类
 */
public abstract class JdbcAbstractDriver extends AbstractDriver {

    private DruidDataSource dataSource;

    @Override
    public void create(Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("username"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("password"))) throw new DataValidateException();

        DruidDataSource dataSource = new DruidDataSource(); // 创建Druid连接池
        dataSource.setDriverClassName(driverClassName());
        dataSource.setUrl(url(properties)); // 设置数据库的连接地址
        dataSource.setUsername(properties.getString("username")); // 数据库的用户名
        dataSource.setPassword(properties.getString("password")); // 数据库的密码
        dataSource.setInitialSize(properties.getInteger("initSize", 8)); // 设置连接池的初始大小
        dataSource.setMinIdle(properties.getInteger("minIdle", 1)); // 设置连接池大小的下限
        dataSource.setMaxActive(properties.getInteger("maxActive", 20)); // 设置连接池大小的上限
        dataSource.setValidationQuery(validationQuery());
        this.dataSource = dataSource;
    }


    @Override
    public void destroy() throws Exception {
        dataSource.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("username"))) return false;
        if (StringUtils.isEmpty(properties.getString("password"))) return false;

        try {
            Class.forName(driverClassName());
            DriverManager.getConnection(url(properties), properties.getString("username"), properties.getString("password"));
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("jdbc driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void handleData(Object data, Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            this.read(data, output);
        } else if (driverMode.equals(DriverModeEnum.TARGET)) {
            this.write(data, output);
        }
    }

    private void read(Object data, Consumer<Object> output) throws Exception {
        String sql = properties.getString("sql");
        if (StringUtils.isEmpty(sql)) throw new DataValidateException();

        List<Entity> result = null;
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                sql = ExpressionUtil.analysis(sql, GlobalVariableContent.getAllValueWith(data));
                result = SqlExecutor.query(connection, sql, new EntityListHandler());
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("jdbc driver error {}", e.getMessage());
            throw e;
        }

        String transferType = properties.getString("transferType", "single");
        if (result != null && transferType.equals("single")) {
            for (Entity entity : result) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("sql", sql);
                map.put("result", entity);
                output.accept(map);
            }
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("sql", sql);
            map.put("result", result);
            output.accept(map);
        }

    }


    private void write(Object data, Consumer<Object> output) throws Exception {
        String sql = properties.getString("sql");
        if (StringUtils.isEmpty(sql)) throw new DataValidateException();
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                sql = ExpressionUtil.analysis(sql, GlobalVariableContent.getAllValueWith(data));
                connection.createStatement().execute(sql);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("jdbc driver error {}", e.getMessage());
            throw e;
        }
    }


    public abstract String url(ConfigProperties properties);

    public abstract String driverClassName();

    public abstract String validationQuery();


}
