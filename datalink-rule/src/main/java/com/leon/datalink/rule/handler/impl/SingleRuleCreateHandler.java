package com.leon.datalink.rule.handler.impl;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.leon.datalink.node.NodeActorCreateParam;
import com.leon.datalink.node.NodeActorCreator;
import com.leon.datalink.node.constans.NodeActorEnum;
import com.leon.datalink.node.graph.GraphNode;
import com.leon.datalink.node.graph.NodeData;
import com.leon.datalink.rule.handler.AbstractRuleCreateHandler;

/**
 * 单机模式下规则启动处理
 */
public class SingleRuleCreateHandler extends AbstractRuleCreateHandler {

    @Override
    protected ActorRef createActor(GraphNode graphNode,NodeActorCreateParam createParam) {
        NodeData nodeData = graphNode.getData();
        NodeActorEnum nodeActorEnum = NodeActorEnum.valueOf(nodeData.getCode());
        return context.actorOf(Props.create(new NodeActorCreator(nodeActorEnum.getNodeActor(),createParam)), graphNode.getId());
    }

}
