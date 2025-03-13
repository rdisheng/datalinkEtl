package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.MqttClientFactory;
import com.leon.datalink.resource.util.mqtt.MqttPoolConfig;
import com.leon.datalink.resource.util.mqtt.MqttTemplate;
import com.leon.datalink.resource.util.mqtt.client.IMqttCallback;
import com.leon.datalink.resource.util.mqtt.client.IMqttClient;
import com.leon.datalink.resource.util.mqtt.entity.MqttMessageEntity;
import com.leon.datalink.resource.util.mqtt.entity.MqttSubParam;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;


public class MqttDriver extends AbstractDriver {

    private IMqttClient mqttClient;

    private MqttTemplate mqttTemplate;


    @Override
    public void create(Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("topic"))) throw new DataValidateException();

        Integer version = properties.getInteger("version", 3);

        MqttClientConfig mqttClientConfig = this.createMqttClientConfig(properties);
        MqttClientFactory mqttClientFactory = new MqttClientFactory(mqttClientConfig);

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttClient = mqttClientFactory.create();
            mqttClient.setCallback(new IMqttCallback() {
                @Override
                public void exceptionOccurred(String exceptionMessage) {
                    Loggers.DRIVER.error("mqtt driver connectionLost {}", exceptionMessage);
                }

                @Override
                public void messageArrived(MqttMessageEntity mqttMessage) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("topic", mqttMessage.getTopic());
                    data.put("qos", mqttMessage.getQos());
                    data.put("retained", mqttMessage.getRetain());
                    data.put("payload", new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
                    if (version == 5) {
                        data.put("userProperties", mqttMessage.getUserProperties());
                        data.put("contentType", mqttMessage.getContentType());
                        data.put("responseTopic", mqttMessage.getResponseTopic());
                        data.put("correlationData", mqttMessage.getCorrelationData());
                        data.put("subscriptionIdentifiers", mqttMessage.getSubscriptionIdentifiers());
                    }
                    output.accept(data);
                }
            });
            Integer qos = properties.getInteger("qos", 0);
            String topic = properties.getString("topic");
            Boolean noLocal = properties.getBoolean("noLocal", false);
            Boolean retainAsPublished = properties.getBoolean("retainAsPublished", false);
            Integer retainHandling = properties.getInteger("retainHandling", 0);
            Integer subscriptionIdentifier = properties.getInteger("subscriptionIdentifier");
            MqttSubParam[] mqttSubParams = Arrays.stream(topic.split(",")).map(subTopic -> {
                if (version == 5) {
                    return new MqttSubParam(subTopic, qos, noLocal, retainAsPublished, retainHandling, subscriptionIdentifier);
                } else {
                    return new MqttSubParam(subTopic, qos);
                }
            }).toArray(MqttSubParam[]::new);
            mqttClient.subscribe(mqttSubParams);

        } else if (driverMode.equals(DriverModeEnum.TARGET)) {
            MqttPoolConfig mqttPoolConfig = new MqttPoolConfig();
            mqttPoolConfig.setMaxTotal(properties.getInteger("maxTotal", 8));
            mqttPoolConfig.setMaxIdle(properties.getInteger("maxIdle", 8));
            mqttPoolConfig.setMinIdle(properties.getInteger("minIdle", 4));
            mqttTemplate = new MqttTemplate(mqttClientFactory, mqttPoolConfig);
        }
    }

    private MqttClientConfig createMqttClientConfig(ConfigProperties properties) {
        MqttClientConfig mqttClientConfig = new MqttClientConfig();
        mqttClientConfig.setHostUrl(properties.getString("url"));
        mqttClientConfig.setUsername(properties.getString("username"));
        mqttClientConfig.setPassword(properties.getString("password", ""));
        mqttClientConfig.setConnectionTimeout(properties.getInteger("connectionTimeout", 10));
        mqttClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 30));
        mqttClientConfig.setAutoReconnect(properties.getBoolean("autoReconnect", true));
        mqttClientConfig.setSsl(properties.getBoolean("ssl", false));
        Integer version = properties.getInteger("version", 3);
        mqttClientConfig.setMqttVersion(version);
        if (version == 5) {
            mqttClientConfig.setCleanStart(properties.getBoolean("cleanStart", true));
            mqttClientConfig.setSessionExpiryInterval(properties.getLong("sessionExpiryInterval"));
            mqttClientConfig.setMaximumPacketSize(properties.getLong("maximumPacketSize"));
            mqttClientConfig.setReceiveMaximum(properties.getInteger("receiveMaximum", 65535));
            mqttClientConfig.setTopicAliasMaximum(properties.getInteger("topicAliasMaximum", 0));
            mqttClientConfig.setRequestProblemInfo(properties.getBoolean("requestProblemInfo", true));
            mqttClientConfig.setRequestResponseInfo(properties.getBoolean("requestResponseInfo", false));
        } else {
            mqttClientConfig.setCleanSession(properties.getBoolean("cleanSession", true));
        }
        return mqttClientConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttClient.disconnectClient();
            mqttClient.closeClient();
        } else if (driverMode.equals(DriverModeEnum.TARGET)) {
            mqttTemplate.close();
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        String url = properties.getString("url");
        if (StringUtils.isEmpty(url)) return false;
        try {
            MqttClientConfig mqttClientConfig = this.createMqttClientConfig(properties);
            MqttClientFactory mqttClientFactory = new MqttClientFactory(mqttClientConfig);
            IMqttClient mqttClient = mqttClientFactory.create();
            return mqttClient.connected();
        } catch (Exception e) {
            Loggers.DRIVER.error("mqtt driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void handleData(Object data,Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            throw new UnsupportedOperationException();
        }
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
        String topic = properties.getString("topic");

        // topic模板解析
        topic = ExpressionUtil.analysis(topic, variable);

        Integer qos = properties.getInteger("qos", 0);
        Boolean retained = properties.getBoolean("retained", false);
        Integer version = properties.getInteger("version", 3);

        // 发布消息
        MqttMessageEntity mqttMessage = new MqttMessageEntity();
        mqttMessage.setTopic(topic);
        mqttMessage.setQos(qos);
        mqttMessage.setRetain(retained);

        if (version == 5) {
            mqttMessage.setPayloadFormat(properties.getBoolean("payloadFormat", false));
            mqttMessage.setContentType(properties.getString("contentType"));
            mqttMessage.setMessageExpiryInterval(properties.getLong("messageExpiryInterval"));
            mqttMessage.setTopicAlias(properties.getInteger("topicAlias"));
            mqttMessage.setResponseTopic(properties.getString("responseTopic"));
            mqttMessage.setCorrelationData(properties.getString("correlationData"));
            mqttMessage.setUserProperties(properties.getMap("userProperties"));
            List<Integer> subscriptionIdentifiers = new ArrayList<>();
            Integer subscriptionIdentifier = properties.getInteger("subscriptionIdentifier");
            if (subscriptionIdentifier != null) subscriptionIdentifiers.add(subscriptionIdentifier);
            mqttMessage.setSubscriptionIdentifiers(subscriptionIdentifiers);
            mqttMessage.setPayload(payload.getBytes(StandardCharsets.UTF_8));
        } else {
            mqttMessage.setPayload(payload.getBytes(StandardCharsets.UTF_8));
        }

        mqttTemplate.publish(mqttMessage);
    }

}
