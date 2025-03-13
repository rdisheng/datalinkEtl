package com.leon.datalink.resource.util.opcua;


public class OpcUAClientConfig {

    private String ip;

    private Integer port;

    private String username;

    private String password;

    private Integer connectTimeout;

    private Integer requestTimeout;

    private Integer keepAliveInterval;

    private Integer keepAliveTimeout;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public Integer getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(Integer keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
}
