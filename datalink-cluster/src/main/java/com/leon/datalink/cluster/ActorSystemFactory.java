package com.leon.datalink.cluster;

import akka.actor.ActorSystem;
import akka.event.Logging;
import com.leon.datalink.cluster.actor.ClusterListenerActor;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.monitor.ListenerContent;
import com.leon.datalink.core.monitor.ListenerTypeEnum;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ActorSystemFactory
 *
 * @author liyang
 */
public class ActorSystemFactory {

    /**
     * 创建ActorSystem
     *
     * @return ActorSystem
     */
    public static ActorSystem create() {
        ActorSystem actorSystem;
        if (EnvUtil.isCluster()) {
            actorSystem = ActorSystem.create("datalink", getConfig());
            actorSystem.actorOf(ClusterListenerActor.props(), "datalinkClusterListener");
        } else {
            actorSystem = ActorSystem.create("datalink");
        }
        actorSystem.eventStream().setLogLevel(Logging.ErrorLevel());
        return actorSystem;
    }

    /**
     * 获取配置
     */
    private static Config getConfig() {

        String[] memberArray = EnvUtil.getClusterMemberList().split(",");
        String[] local = memberArray[0].split(":");
        String ip = local[0];
        String port = local[1];

        Map<String, Object> map = new HashMap<>();
        map.put("akka.actor.provider", "cluster");

        map.put("akka.actor.allow-java-serialization", true);

        map.put("akka.actor.serializers.protostuff", "com.leon.datalink.core.serializer.ProtostuffSerializer");
        map.put("akka.actor.serialization-bindings.\"com.leon.datalink.core.serializer.ProtostuffSerializable\"", "protostuff");

        map.put("akka.remote.artery.enabled", "on");
        map.put("akka.remote.artery.transport", "tcp");
        map.put("akka.remote.artery.canonical.hostname", ip);
        map.put("akka.remote.artery.canonical.port", port);

        List<String> nodes = Arrays.stream(memberArray).map(member -> "akka://datalink@" + member).collect(Collectors.toList());
        map.put("akka.cluster.seed-nodes", nodes);

        // 加入监听器
        ListenerContent.add(ip, Integer.parseInt(port), ListenerTypeEnum.TCP, "Datalink cluster port");

        return ConfigFactory.parseMap(map);
    }

}
