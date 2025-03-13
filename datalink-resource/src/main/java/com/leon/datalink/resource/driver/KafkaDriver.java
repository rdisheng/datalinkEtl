package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

public class KafkaDriver extends AbstractDriver {

    private KafkaConsumer<String, String> kafkaConsumer;

    private KafkaProducer<String, String> kafkaProducer;

    private boolean isClose = false;

    @Override
    public void create(Consumer<Object> output) throws Exception {
        String url = properties.getString("url");
        if (StringUtils.isEmpty(url)) throw new DataValidateException();

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            String topic = properties.getString("topic");
            if (StringUtils.isEmpty(topic)) throw new DataValidateException();
            String group = properties.getString("group");

            Properties prop = new Properties();
            prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
            prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            if (!StringUtils.isEmpty(group)) prop.put(ConsumerConfig.GROUP_ID_CONFIG, group);
            this.kafkaConsumer = new KafkaConsumer<>(prop);
            kafkaConsumer.subscribe(Collections.singletonList(topic));
            new Thread(() -> {
                while (true) {
                    if (isClose) {
                        kafkaConsumer.close();
                        return;
                    }
                    final ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("topic", record.topic());
                        data.put("payload", record.value());
                        output.accept(data);
                    }
                }
            }).start();
        } else {
            Properties prop = new Properties();
            prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
            prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            this.kafkaProducer = new KafkaProducer<>(prop);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            this.isClose = true;
        } else {
            this.kafkaProducer.close();
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        String url = properties.getString("url");
        if (StringUtils.isEmpty(url)) return false;
        try {
            Properties prop = new Properties();
            prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
            prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            new KafkaProducer<>(prop);
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void handleData(Object data, Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            throw new UnsupportedOperationException();
        }
        String topic = properties.getString("topic");
        if (StringUtils.isEmpty(topic)) throw new DataValidateException();

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // 消息模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            payload = ExpressionUtil.analysis(payload, variable);
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        // topic模板解析
        topic = ExpressionUtil.analysis(topic, variable);

        // 发布消息
        kafkaProducer.send(new ProducerRecord<>(topic, payload));
    }

}
