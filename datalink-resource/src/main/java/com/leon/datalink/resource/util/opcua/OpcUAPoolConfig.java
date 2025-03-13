package com.leon.datalink.resource.util.opcua;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

public class OpcUAPoolConfig extends GenericObjectPoolConfig<OpcUaClient> {

    public OpcUAPoolConfig() {
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
        this.setBlockWhenExhausted(true);
        this.setTestOnBorrow(true);
        this.setTestWhileIdle(true);
        this.setJmxEnabled(false);
    }

}
