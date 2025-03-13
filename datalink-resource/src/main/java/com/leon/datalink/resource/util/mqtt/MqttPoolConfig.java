package com.leon.datalink.resource.util.mqtt;

import com.leon.datalink.resource.util.mqtt.client.IMqttClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class MqttPoolConfig extends GenericObjectPoolConfig<IMqttClient> {

    public MqttPoolConfig() {
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
        this.setBlockWhenExhausted(true);
        this.setTestOnBorrow(true);
        this.setTestWhileIdle(true);
        this.setJmxEnabled(false);
    }

}
