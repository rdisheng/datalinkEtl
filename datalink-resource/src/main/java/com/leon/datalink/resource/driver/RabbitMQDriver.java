package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.rabbitmq.client.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitMQDriver extends AbstractDriver {

    Connection connection;

    @Override
    public void create(java.util.function.Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getInteger("port"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("virtualHost"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("username"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("password"))) throw new DataValidateException();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.getString("ip"));
        factory.setPort(properties.getInteger("port"));
        factory.setVirtualHost(properties.getString("virtualHost"));
        factory.setUsername(properties.getString("username"));
        factory.setPassword(properties.getString("password"));

        connection = factory.newConnection();
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (StringUtils.isEmpty(properties.getString("queue"))) throw new DataValidateException();
            Channel channel = connection.createChannel();
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("consumerTag", consumerTag);
                    map.put("payload", new String(body));
                    map.put("driver", properties);
                    output.accept(map);
                }
            };
            channel.basicConsume(properties.getString("queue"), true, consumer);
        }

    }

    @Override
    public void destroy() throws Exception {
        connection.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getInteger("port"))) return false;
        if (StringUtils.isEmpty(properties.getString("virtualHost"))) return false;
        if (StringUtils.isEmpty(properties.getString("username"))) return false;
        if (StringUtils.isEmpty(properties.getString("password"))) return false;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getString("ip"));
            factory.setPort(properties.getInteger("port"));
            factory.setVirtualHost(properties.getString("virtualHost"));
            factory.setUsername(properties.getString("username"));
            factory.setPassword(properties.getString("password"));
            Connection connection = factory.newConnection();
            return connection.isOpen();
        } catch (Exception e) {
            Loggers.DRIVER.error("rabbitmq driver driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void handleData(Object data,java.util.function.Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            throw new UnsupportedOperationException();
        }
        String queue = properties.getString("queue");
        if (StringUtils.isEmpty(queue)) throw new DataValidateException();

        String exchange = properties.getString("exchange");
        if (StringUtils.isEmpty(exchange)) throw new DataValidateException();

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // queue模板解析
        queue = ExpressionUtil.analysis(queue, variable);

        // 消息模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            payload = ExpressionUtil.analysis(payload, variable);
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        Channel channel = connection.createChannel();
        channel.basicPublish(exchange, queue, null, payload.getBytes());
    }

}
