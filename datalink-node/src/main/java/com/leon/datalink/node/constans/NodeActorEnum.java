package com.leon.datalink.node.constans;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.actor.*;
import com.leon.datalink.node.behavior.NodeClusterBehavior;
import com.leon.datalink.node.behavior.NodeClusterBehaviorEnum;


public enum NodeActorEnum {

    MQTT_SUBSCRIBE(DriverNodeActor.class, prop -> prop.getBoolean("share", false) ? NodeClusterBehaviorEnum.MULTI : NodeClusterBehaviorEnum.SINGLE),
    MQTT_PUBLISH(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    KAFKA_SUBSCRIBE(DriverNodeActor.class, prop -> StringUtils.isEmpty(prop.getString("group")) ? NodeClusterBehaviorEnum.SINGLE : NodeClusterBehaviorEnum.MULTI),
    KAFKA_PUBLISH(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    PULSAR_SUBSCRIBE(DriverNodeActor.class, prop -> prop.getString("subscriptionType", "Exclusive").equals("Exclusive") ? NodeClusterBehaviorEnum.SINGLE : NodeClusterBehaviorEnum.MULTI),
    PULSAR_PUBLISH(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    ROCKETMQ_SUBSCRIBE(DriverNodeActor.class, prop -> prop.getString("model", "BROADCASTING").equals("BROADCASTING") ? NodeClusterBehaviorEnum.SINGLE : NodeClusterBehaviorEnum.MULTI),
    ROCKETMQ_PUBLISH(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    ACTIVEMQ_SUBSCRIBE(DriverNodeActor.class, prop -> prop.getString("model", "topic").equals("topic") ? NodeClusterBehaviorEnum.SINGLE : NodeClusterBehaviorEnum.MULTI),
    ACTIVEMQ_PUBLISH(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    RABBITMQ_SUBSCRIBE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    RABBITMQ_PUBLISH(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    TCP_LISTEN(TcpNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    UDP_LISTEN(UdpNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    HTTP_CLIENT(HttpClientNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    HTTP_SERVER(HttpServerNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),
    HTTP_RESPONSE(HttpResponseNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    COAP_CLIENT(CoapClientNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    COAP_SERVER(CoapServerNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    FILE_WATCH(FileWatchNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),
    FILE_WRITE(FileWriteNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    MYSQL_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    MYSQL_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    POSTGRESQL_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    POSTGRESQL_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    TDENGINE_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    TDENGINE_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    SQLSERVER_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    SQLSERVERE_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    TIMESCALEDB_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    TIMESCALEDB_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    MARIADB_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    MARIADB_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    DM8_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    DM8_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    KINGBASE_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    KINGBASE_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    REDIS_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    REDIS_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    OPCUA_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    OPCUA_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    SNMP_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    MODBUSTCP_READ(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),
    MODBUSTCP_WRITE(DriverNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    PLUGIN(PluginNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    JAVASCRIPT(ScriptNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    COMMAND(CommandNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    GROOVY(ScriptNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    TEMPLATE(TemplateNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    SWITCH(SwitchNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    FILTER(FilterNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    DELAY(DelayNodeActor.class, prop -> NodeClusterBehaviorEnum.MULTI),

    SCHEDULE(ScheduleNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    SPEED(SpeedNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE),

    PACK(PackNodeActor.class, prop -> NodeClusterBehaviorEnum.SINGLE);

    private final Class<? extends AbstractNodeActor> nodeActor;

    private final NodeClusterBehavior behavior;

    NodeActorEnum(Class<? extends AbstractNodeActor> nodeActor, NodeClusterBehavior behavior) {
        this.nodeActor = nodeActor;
        this.behavior = behavior;
    }

    public Class<? extends AbstractNodeActor> getNodeActor() {
        return nodeActor;
    }

    public NodeClusterBehaviorEnum getBehavior(ConfigProperties configProperties) {
        return behavior.get(configProperties);
    }
}
