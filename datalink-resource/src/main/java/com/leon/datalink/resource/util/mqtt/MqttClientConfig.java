package com.leon.datalink.resource.util.mqtt;


public class MqttClientConfig {

    private String hostUrl;

    private String username;

    private String password;

    private Integer connectionTimeout;

    private Integer keepAliveInterval;

    private Boolean ssl;

    private Boolean autoReconnect;

    private Integer mqttVersion;

    private Boolean cleanSession;

    private Boolean cleanStart;

    private Long sessionExpiryInterval; // 会话过期时间

    private Long maximumPacketSize; // 最大数据包大小

    private Integer receiveMaximum; // 接收最大数值

    private Integer topicAliasMaximum; // 主题别名最大值

    private Boolean requestProblemInfo; // 请求失败信息

    private Boolean requestResponseInfo; // 请求响应信息

    public MqttClientConfig() {
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public MqttClientConfig setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public MqttClientConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MqttClientConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public MqttClientConfig setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public MqttClientConfig setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    public Boolean getAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(Boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public Integer getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(Integer mqttVersion) {
        this.mqttVersion = mqttVersion;
    }

    public Boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public Boolean getCleanStart() {
        return cleanStart;
    }

    public void setCleanStart(Boolean cleanStart) {
        this.cleanStart = cleanStart;
    }

    public Long getSessionExpiryInterval() {
        return sessionExpiryInterval;
    }

    public void setSessionExpiryInterval(Long sessionExpiryInterval) {
        this.sessionExpiryInterval = sessionExpiryInterval;
    }

    public Long getMaximumPacketSize() {
        return maximumPacketSize;
    }

    public void setMaximumPacketSize(Long maximumPacketSize) {
        this.maximumPacketSize = maximumPacketSize;
    }

    public Integer getReceiveMaximum() {
        return receiveMaximum;
    }

    public void setReceiveMaximum(Integer receiveMaximum) {
        this.receiveMaximum = receiveMaximum;
    }

    public Integer getTopicAliasMaximum() {
        return topicAliasMaximum;
    }

    public void setTopicAliasMaximum(Integer topicAliasMaximum) {
        this.topicAliasMaximum = topicAliasMaximum;
    }

    public Boolean getRequestProblemInfo() {
        return requestProblemInfo;
    }

    public void setRequestProblemInfo(Boolean requestProblemInfo) {
        this.requestProblemInfo = requestProblemInfo;
    }

    public Boolean getRequestResponseInfo() {
        return requestResponseInfo;
    }

    public void setRequestResponseInfo(Boolean requestResponseInfo) {
        this.requestResponseInfo = requestResponseInfo;
    }
}
