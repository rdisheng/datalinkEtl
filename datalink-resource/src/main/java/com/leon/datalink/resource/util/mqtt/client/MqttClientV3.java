package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.utils.SSLUtils;
import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.MqttClientFactory;
import com.leon.datalink.resource.util.mqtt.entity.MqttMessageEntity;
import com.leon.datalink.resource.util.mqtt.entity.MqttSubParam;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InputStream;
import java.util.UUID;

public class MqttClientV3 extends MqttClient implements IMqttClient {

    public MqttClientV3(MqttClientConfig mqttClientConfig) throws Exception {
        super(mqttClientConfig.getHostUrl(), UUID.randomUUID().toString(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        // mqtt版本
        options.setMqttVersion(mqttClientConfig.getMqttVersion());
        // 如果想要断线这段时间的数据，要设置成false，并且重连后不用再次订阅，否则不会得到断线时间的数据
        options.setCleanSession(mqttClientConfig.getCleanSession());
        // 增加 actualInFlight 的值
        options.setMaxInflight(1000);
        // 自动重连
        options.setAutomaticReconnect(mqttClientConfig.getAutoReconnect());
        // 设置连接的用户名
        options.setUserName(mqttClientConfig.getUsername());
        // 设置连接的密码
        options.setPassword(mqttClientConfig.getPassword().toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(mqttClientConfig.getConnectionTimeout());
        // 设置会话心跳时间 单位为秒
        options.setKeepAliveInterval(mqttClientConfig.getKeepAliveInterval());
        // 是否开启ssl
        if (mqttClientConfig.getSsl()) {
            InputStream resourceAsStream = MqttClientFactory.class.getClassLoader().getResourceAsStream(EnvUtil.getCaCrtFile());
            options.setSocketFactory(SSLUtils.getSocketFactory(resourceAsStream));
        }
        // 连接服务器
        super.connect(options);
    }

    @Override
    public void publish(MqttMessageEntity message) throws Exception {
        super.publish(message.getTopic(), message.getPayload(), message.getQos(), message.getRetain());
    }

    @Override
    public void subscribe(MqttSubParam[] subParams) throws Exception {
        String[] topic = new String[subParams.length];
        int[] qos = new int[subParams.length];
        for (int i = 0; i < subParams.length; i++) {
            topic[i] = subParams[i].getTopic();
            qos[i] = subParams[i].getQos();
        }
        super.subscribe(topic, qos);
    }

    @Override
    public boolean connected() {
        return super.isConnected();
    }

    @Override
    public void disconnectClient() throws Exception {
        super.disconnect();
    }

    @Override
    public void closeClient() throws Exception {
        super.close();
    }

    @Override
    public void setCallback(IMqttCallback mqttCallback) {
        super.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                mqttCallback.exceptionOccurred(cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                MqttMessageEntity mqttMessageEntity = new MqttMessageEntity();
                mqttMessageEntity.setTopic(topic);
                mqttMessageEntity.setQos(message.getQos());
                mqttMessageEntity.setPayload(message.getPayload());
                mqttMessageEntity.setRetain(message.isRetained());

                mqttCallback.messageArrived(mqttMessageEntity);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

}
