package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.util.StringUtils;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ActiveMQDriver extends AbstractDriver {

    Connection connection;

    Session session;

    MessageConsumer consumer;

    @Override
    public void create(Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("model"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString(properties.getString("model")))) throw new DataValidateException();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                properties.getString("username", ActiveMQConnectionFactory.DEFAULT_USER),
                properties.getString("password", ActiveMQConnectionFactory.DEFAULT_PASSWORD),
                properties.getString("url"));
        connection = connectionFactory.createConnection();
        session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        connection.start();

        String model = properties.getString("model");
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            Destination destination;
            if ("queue".equals(model)) {
                destination = session.createQueue(properties.getString("queue"));
            } else {
                destination = session.createTopic(properties.getString("topic"));
            }
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("payload", textMessage.getText());
                    map.put("driver", properties);
                    output.accept(map);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            consumer.close();
        }
        session.close();
        connection.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new DataValidateException();
        Connection connection = null;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    properties.getString("username", ActiveMQConnectionFactory.DEFAULT_USER),
                    properties.getString("password", ActiveMQConnectionFactory.DEFAULT_PASSWORD),
                    properties.getString("url"));
            connection = connectionFactory.createConnection();
            connection.start();
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("activemq driver test {}", e.getMessage());
            return false;
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    Loggers.DRIVER.error("activemq driver test {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void handleData(Object data,Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            throw new UnsupportedOperationException();
        }

        String model = properties.getString("model"); // queue or topic
        String dest = properties.getString(model);

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // queue或topic模板解析
        dest = ExpressionUtil.analysis(dest, variable);

        // 消息模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            payload = ExpressionUtil.analysis(payload, variable);
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        Destination destination;
        if ("queue".equals(model)) {
            destination = session.createQueue(dest);
        } else {
            destination = session.createTopic(dest);
        }
        MessageProducer producer = session.createProducer(destination);
        TextMessage textMessage = session.createTextMessage(payload);
        producer.send(textMessage);
        producer.close();
    }

}
