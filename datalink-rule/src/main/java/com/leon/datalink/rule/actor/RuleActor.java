package com.leon.datalink.rule.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.node.message.NodeOutRefGetMessage;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.handler.RuleCreateHandler;
import com.leon.datalink.rule.handler.impl.ClusterRuleCreateHandler;
import com.leon.datalink.rule.handler.impl.SingleRuleCreateHandler;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.message.RuntimeData;
import com.leon.datalink.runtime.message.RuntimeStatus;

import java.util.List;
import java.util.Map;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private Map<String, Map<Integer, List<ActorRef>>> nodeOutRefMap;

    public static Props props(Rule rule) {
        return Props.create(RuleActor.class, () -> new RuleActor(rule));
    }

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    /**
     * 启动rule
     */
    @Override
    public void preStart() {
        Loggers.RULE.info("start rule [{}]", getSelf().path());
        RuleCreateHandler ruleCreateHandler;
        if (EnvUtil.isCluster()) {
            ruleCreateHandler = new ClusterRuleCreateHandler();
        } else {
            ruleCreateHandler = new SingleRuleCreateHandler();
        }
        nodeOutRefMap = ruleCreateHandler.create(rule, getContext());
    }

    @Override
    public void postStop() {
        Loggers.RULE.info("stop rule [{}]", getSelf().path());
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NodeOutRefGetMessage.class, nodeOutRefGetMessage -> {
                    nodeOutRefGetMessage.setOutRef(nodeOutRefMap.get(nodeOutRefGetMessage.getNodeId()));
                    getSender().tell(nodeOutRefGetMessage, getSelf());
                })
                .match(RuntimeStatus.class, runtimeStatus -> RuntimeManger.handleStatus(rule.getRuleId(), runtimeStatus))
                .match(RuntimeData.class, runtimeData -> RuntimeManger.handleData(rule.getRuleId(), runtimeData))
                .build();
    }


}
