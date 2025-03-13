package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.resource.util.mqtt.entity.MqttMessageEntity;
import com.leon.datalink.resource.util.mqtt.entity.MqttSubParam;

public interface IMqttClient {

    void publish(MqttMessageEntity message) throws Exception;

    void subscribe(MqttSubParam[] subParams) throws Exception;

    boolean connected();

    void disconnectClient() throws Exception;

    void closeClient() throws Exception;

    void setCallback(IMqttCallback mqttCallback);

}
