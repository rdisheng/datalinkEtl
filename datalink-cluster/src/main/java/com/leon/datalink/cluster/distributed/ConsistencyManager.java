package com.leon.datalink.cluster.distributed;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.leon.datalink.cluster.actor.VariableConsistencyActor;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public class ConsistencyManager {

    private static final Map<Class<?>, ActorRef> actors = new HashMap<>();

    public static void init(ActorSystem actorSystem) {
        if (!EnvUtil.isCluster()) return;
        // Variable 同步actor
        actors.put(Variable.class, actorSystem.actorOf(Props.create(VariableConsistencyActor.class), "variableConsistencyActor"));
        //...
    }

    /**
     * 成员上线时
     */
    public static void onMemberUp() {
        if (!EnvUtil.isCluster()) return;
        actors.forEach((key, value) -> value.tell(ConsistencyWrapper.init(key), ActorRef.noSender()));
    }

    public static void onSystemDestroy(){
        if (!EnvUtil.isCluster()) return;
        actors.forEach((key, value) -> value.tell(ConsistencyWrapper.destroy(key), ActorRef.noSender()));
    }

    public static void sync(ConsistencyWrapper wrapper) {
        if (!EnvUtil.isCluster()) return;
        actors.get(wrapper.getClazz()).tell(wrapper, ActorRef.noSender());
    }

}
