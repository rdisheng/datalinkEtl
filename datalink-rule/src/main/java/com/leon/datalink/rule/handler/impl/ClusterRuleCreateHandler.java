package com.leon.datalink.rule.handler.impl;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.routing.RoundRobinPool;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.node.NodeActorCreateParam;
import com.leon.datalink.node.NodeActorCreator;
import com.leon.datalink.node.behavior.NodeClusterBehaviorEnum;
import com.leon.datalink.node.constans.NodeActorEnum;
import com.leon.datalink.node.graph.GraphNode;
import com.leon.datalink.node.graph.NodeData;
import com.leon.datalink.rule.handler.AbstractRuleCreateHandler;

import java.util.HashSet;

/**
 * 集群模式下规则启动处理
 */
public class ClusterRuleCreateHandler extends AbstractRuleCreateHandler {

    @Override
    protected ActorRef createActor(GraphNode graphNode, NodeActorCreateParam createParam) {
        NodeData nodeData = graphNode.getData();

        NodeActorEnum nodeActorEnum = NodeActorEnum.valueOf(nodeData.getCode());
        NodeClusterBehaviorEnum behavior = nodeActorEnum.getBehavior(nodeData.getProperties());

        Props props = Props.create(new NodeActorCreator(nodeActorEnum.getNodeActor(), createParam));

        ActorRef actorRef = null;
        switch (behavior) {
            case SINGLE: {
                // 仅在本地节点
                actorRef = context.actorOf(props, graphNode.getId());
                break;
            }
            case MULTI: {
                // 集群下每个节点
                actorRef = context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                                new ClusterRouterPoolSettings(EnvUtil.getClusterInstancesMax(), 1, true, new HashSet<>()))
                                .props(props),
                        graphNode.getId());
                break;
            }
        }
        return actorRef;

    }


}
