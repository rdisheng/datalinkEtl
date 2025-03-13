package com.leon.datalink.resource.util.mqtt;

import com.leon.datalink.resource.util.mqtt.client.IMqttClient;
import com.leon.datalink.resource.util.mqtt.entity.MqttMessageEntity;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class MqttTemplate extends GenericObjectPool<IMqttClient> {

    public MqttTemplate(PooledObjectFactory<IMqttClient> factory, GenericObjectPoolConfig<IMqttClient> config) {
        super(factory, config);
    }

    public void publish(MqttMessageEntity mqttMessage) throws Exception {
        IMqttClient client = null;
        try {
            client = super.borrowObject();
            if (client.connected()) {
                client.publish(mqttMessage);
            }
        } finally {
            if (client != null) {
                super.returnObject(client);
            }
        }
    }
}
