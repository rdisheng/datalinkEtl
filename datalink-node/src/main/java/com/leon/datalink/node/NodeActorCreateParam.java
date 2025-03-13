package com.leon.datalink.node;

import akka.actor.ActorRef;
import com.leon.datalink.core.config.ConfigProperties;

import java.io.Serializable;

public class NodeActorCreateParam implements Serializable {

    private String nodeId;

    private ActorRef ruleRef;

    private ConfigProperties properties;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public ActorRef getRuleRef() {
        return ruleRef;
    }

    public void setRuleRef(ActorRef ruleRef) {
        this.ruleRef = ruleRef;
    }

    public ConfigProperties getProperties() {
        return properties;
    }

    public void setProperties(ConfigProperties properties) {
        this.properties = properties;
    }

}
