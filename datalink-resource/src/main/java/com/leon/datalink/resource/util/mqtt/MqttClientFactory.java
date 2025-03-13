package com.leon.datalink.resource.util.mqtt;

import com.leon.datalink.resource.util.mqtt.client.IMqttClient;
import com.leon.datalink.resource.util.mqtt.client.MqttClientV3;
import com.leon.datalink.resource.util.mqtt.client.MqttClientV5;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MqttClientFactory extends BasePooledObjectFactory<IMqttClient> {

    private final MqttClientConfig mqttClientConfig;

    public MqttClientFactory(MqttClientConfig mqttClientConfig) {
        this.mqttClientConfig = mqttClientConfig;
    }

    @Override
    public IMqttClient create() throws Exception {
        return mqttClientConfig.getMqttVersion() == 5 ? new MqttClientV5(mqttClientConfig) : new MqttClientV3(mqttClientConfig);
    }

    @Override
    public PooledObject<IMqttClient> wrap(IMqttClient mqttClient) {
        return new DefaultPooledObject<>(mqttClient);
    }

    @Override
    public void destroyObject(PooledObject<IMqttClient> pooledMqttClient) throws Exception {
        if (pooledMqttClient != null && pooledMqttClient.getObject() != null && pooledMqttClient.getObject().connected()) {
            pooledMqttClient.getObject().disconnectClient();
        }
    }

    @Override
    public boolean validateObject(PooledObject<IMqttClient> pooledMqttClient) {
        return pooledMqttClient.getObject().connected();
    }
}
