package com.leon.datalink.resource.constans;


import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.driver.*;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class),
    KAFKA(KafkaDriver.class),
    ROCKETMQ(RocketMQDriver.class),
    ACTIVEMQ(ActiveMQDriver.class),
    RABBITMQ(RabbitMQDriver.class),

    MYSQL(MysqlDriver.class),
    POSTGRESQL(PostgresqlDriver.class),
    TDENGINE(TDengineDriver.class),
    SQLSERVER(SqlServerDriver.class),
    OPCUA(OpcUADriver.class),
    REDIS(RedisDriver.class),
    SNMP(SnmpDriver.class),
    MODBUSTCP(ModbusTcpDriver.class);


    private final Class<? extends Driver> driver;


    ResourceTypeEnum(Class<? extends Driver> driver) {
        this.driver = driver;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

}
