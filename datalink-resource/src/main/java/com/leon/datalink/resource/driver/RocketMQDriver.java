package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RocketMQDriver extends AbstractDriver {

    DefaultMQProducer producer;

    DefaultMQPushConsumer consumer;

    @Override
    public void create(Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("group"))) throw new DataValidateException();

        if (driverMode.equals(DriverModeEnum.TARGET)) {
            // 生产者
            producer = new DefaultMQProducer(properties.getString("group"));
            producer.setNamesrvAddr(properties.getString("url"));
            producer.start();
        } else if (driverMode.equals(DriverModeEnum.SOURCE)) {
            //消费者
            if (StringUtils.isEmpty(properties.getString("topic"))) throw new DataValidateException();

            consumer = new DefaultMQPushConsumer(properties.getString("group"));
            consumer.setNamesrvAddr(properties.getString("url"));
            consumer.subscribe(properties.getString("topic"), properties.getString("tags", "*"));
            consumer.setMessageModel(MessageModel.valueOf(properties.getString("model", "CLUSTERING")));

            consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
                for (MessageExt msg : list) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("topic", msg.getTopic());
                    map.put("payload", new String(msg.getBody()));
                    map.put("driver", properties);
                    output.accept(map);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.TARGET)) {
            producer.shutdown();
        } else if (driverMode.equals(DriverModeEnum.SOURCE)) {
            consumer.shutdown();
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("url"))) return false;
        if (StringUtils.isEmpty(properties.getString("group"))) return false;

        DefaultMQPushConsumer consumer = null;
        try {
            consumer = new DefaultMQPushConsumer(properties.getString("group"));
            consumer.setNamesrvAddr(properties.getString("url"));
            consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
            consumer.start();
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("rocketmq driver test {}", e.getMessage());
            return false;
        } finally {
            if (null != consumer) {
                consumer.shutdown();
            }
        }
    }

    @Override
    public void handleData(Object data,Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            throw new UnsupportedOperationException();
        }
        String topic = properties.getString("topic");
        if (StringUtils.isEmpty(topic)) throw new DataValidateException();

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // topic模板解析
        topic = ExpressionUtil.analysis(topic, variable);

        // 消息模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            payload = ExpressionUtil.analysis(payload, variable);
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        String tags = properties.getString("tags", "");
        Message msg = new Message(topic, tags, payload.getBytes());

        //发送消息
        producer.send(msg, properties.getInteger("timeout", 10000));
    }

}
