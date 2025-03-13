package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.resource.util.mqtt.entity.MqttMessageEntity;

public interface IMqttCallback {

    void exceptionOccurred(String exceptionMessage);

    void messageArrived(MqttMessageEntity mqttMessage);

}
