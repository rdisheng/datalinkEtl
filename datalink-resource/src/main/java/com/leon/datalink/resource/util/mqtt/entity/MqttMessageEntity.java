package com.leon.datalink.resource.util.mqtt.entity;

import java.util.List;
import java.util.Map;

public class MqttMessageEntity {

    private String topic;

    private byte[] payload;

    private Integer qos;

    private Boolean isRetain;

    private Map<String, String> userProperties;

    private String contentType; // 内容类型

    private Boolean payloadFormat; // 有效载荷指示器

    private Long messageExpiryInterval; // 消息过期时间

    private Integer topicAlias; // 主题别名

    private String responseTopic; // 响应主题

    private String correlationData; // 对比数据

    private List<Integer> subscriptionIdentifiers; // 订阅标识符 发布时 收到消息时

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getRetain() {
        return isRetain;
    }

    public void setRetain(Boolean retain) {
        isRetain = retain;
    }

    public Map<String, String> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Map<String, String> userProperties) {
        this.userProperties = userProperties;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Boolean getPayloadFormat() {
        return payloadFormat;
    }

    public void setPayloadFormat(Boolean payloadFormat) {
        this.payloadFormat = payloadFormat;
    }

    public Long getMessageExpiryInterval() {
        return messageExpiryInterval;
    }

    public void setMessageExpiryInterval(Long messageExpiryInterval) {
        this.messageExpiryInterval = messageExpiryInterval;
    }

    public Integer getTopicAlias() {
        return topicAlias;
    }

    public void setTopicAlias(Integer topicAlias) {
        this.topicAlias = topicAlias;
    }

    public String getResponseTopic() {
        return responseTopic;
    }

    public void setResponseTopic(String responseTopic) {
        this.responseTopic = responseTopic;
    }

    public String getCorrelationData() {
        return correlationData;
    }

    public void setCorrelationData(String correlationData) {
        this.correlationData = correlationData;
    }

    public List<Integer> getSubscriptionIdentifiers() {
        return subscriptionIdentifiers;
    }

    public void setSubscriptionIdentifiers(List<Integer> subscriptionIdentifiers) {
        this.subscriptionIdentifiers = subscriptionIdentifiers;
    }

}
