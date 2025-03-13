package com.leon.datalink.node.message;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NodeOutRefGetMessage implements Serializable {

    private String nodeId;

    private Map<Integer, List<ActorRef>> outRef;

    public NodeOutRefGetMessage() {
    }

    public NodeOutRefGetMessage(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Map<Integer, List<ActorRef>> getOutRef() {
        return outRef;
    }

    public void setOutRef(Map<Integer, List<ActorRef>> outRef) {
        this.outRef = outRef;
    }

}
