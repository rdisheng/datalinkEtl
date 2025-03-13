package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.node.NodeActorCreateParam;
import com.leon.datalink.node.graph.Graph;
import com.leon.datalink.node.graph.GraphNode;
import com.leon.datalink.node.graph.NodePort;
import com.leon.datalink.rule.entity.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractRuleCreateHandler implements RuleCreateHandler {

    protected Rule rule;

    protected ActorContext context;

    @Override
    public final Map<String, Map<Integer, List<ActorRef>>> create(Rule rule, ActorContext context) {
        this.rule = rule;
        this.context = context;

        Graph graph = JacksonUtils.toObj(rule.getGraphJson(), Graph.class);

        List<GraphNode> cells = graph.getCells();
        Map<String, ActorRef> actorMap = new HashMap<>();
        Map<String, GraphNode> graphNodeMap = new HashMap<>();
        Map<String, Map<Integer, List<ActorRef>>> outRefMap = new HashMap<>();

        cells.stream().filter(cell -> !cell.getShape().equals("edge")).forEach(node -> {
            NodeActorCreateParam nodeActorCreateParam = new NodeActorCreateParam();
            nodeActorCreateParam.setNodeId(node.getId());
            nodeActorCreateParam.setRuleRef(context.self());
            nodeActorCreateParam.setProperties(node.getData().getProperties());
            actorMap.put(node.getId(), createActor(node, nodeActorCreateParam));
            graphNodeMap.put(node.getId(), node);
        });

        cells.stream().filter(cell -> cell.getShape().equals("edge")).forEach(edge -> {
            String sourceId = edge.getSource().getCell();
            String targetId = edge.getTarget().getCell();
            List<NodePort> sourceOutPorts = graphNodeMap.get(sourceId).getPorts().getItems().stream().filter(port -> port.getGroup().equals("out")).collect(Collectors.toList());
            sourceOutPorts.stream().filter(port -> port.getId().equals(edge.getSource().getPort())).findFirst().ifPresent(nodePort -> {
                int index = sourceOutPorts.indexOf(nodePort) + 1;
                Map<Integer, List<ActorRef>> outRef = outRefMap.getOrDefault(sourceId, new HashMap<>());
                List<ActorRef> actorRefs = outRef.getOrDefault(index, new ArrayList<>());
                actorRefs.add(actorMap.get(targetId));
                outRef.put(index, actorRefs);
                outRefMap.put(sourceId, outRef);
            });
        });
        return outRefMap;
    }

    protected abstract ActorRef createActor(GraphNode graphNode, NodeActorCreateParam createParam);


}
