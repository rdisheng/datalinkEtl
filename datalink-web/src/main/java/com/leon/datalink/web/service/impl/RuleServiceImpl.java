package com.leon.datalink.web.service.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.leon.datalink.core.backup.BackupData;
import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.IdUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.graph.*;
import com.leon.datalink.rule.actor.RuleActor;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.service.PluginService;
import com.leon.datalink.web.service.ResourceService;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.web.service.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName RuleServiceImpl
 * @Description
 * @Author Leon
 * @Date 2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class RuleServiceImpl implements RuleService, BackupData<Rule> {


    ActorSystem actorSystem;

    @Autowired
    @Lazy
    RuntimeService runtimeService;

    @Autowired
    @Lazy
    PluginService pluginService;

    @Autowired
    @Lazy
    ResourceService resourceService;

    private final ConcurrentHashMap<String, ActorRef> ruleActorRefList = new ConcurrentHashMap<>();

    private final DataStorage<Rule> dataStorage;

    public RuleServiceImpl(ActorSystem actorSystem) throws Exception {
        this.actorSystem = actorSystem;
        this.dataStorage = new ObjectStorage<>(Rule.class);
    }

    @PreDestroy
    public void destroy() {
        ruleActorRefList.values().forEach(ruleActorRef -> actorSystem.stop(ruleActorRef));
    }


    @Override
    public Rule get(String ruleId) {
        return this.dataStorage.get(ruleId).setEnable(ruleActorRefList.containsKey(ruleId));
    }

    @Override
    public void add(Rule rule) {
        List<String> nodeIds = this.checkAndSupplement(rule);
        rule.setRuleId(SnowflakeIdWorker.getId());
        rule.setNodeCount(nodeIds.size());
        rule.setUpdateTime(DateUtil.now());
        rule.setEnable(null);
        this.dataStorage.put(rule.getRuleId(), rule);
        runtimeService.initRuntime(rule.getRuleId(), nodeIds);
    }

    @Override
    public void remove(String ruleId) {
        if (this.get(ruleId).isEnable()) this.stopRule(ruleId);
        this.dataStorage.delete(ruleId);
        runtimeService.removeRuntime(ruleId);
    }

    @Override
    public void update(Rule rule) {
        List<String> nodeIds = this.checkAndSupplement(rule);
        rule.setNodeCount(nodeIds.size());
        rule.setUpdateTime(DateUtil.now());
        rule.setEnable(null);
        this.dataStorage.put(rule.getRuleId(), rule);
        runtimeService.initRuntime(rule.getRuleId(), nodeIds);
    }

    @Override
    public List<Rule> list(Rule rule) {
        Stream<Rule> stream = this.dataStorage.getValues().stream()
                .peek(value -> {
                    value.setEnable(ruleActorRefList.containsKey(value.getRuleId()));
                    value.setGraphJson(null);
                });
        if (null != rule) {
            if (!StringUtils.isEmpty(rule.getRuleName())) {
                stream = stream.filter(r -> r.getRuleName().contains(rule.getRuleName()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getRuleId()))).collect(Collectors.toList()));
    }

    @Override
    public long getCount() {
        return this.dataStorage.count();
    }

    @Override
    public void startRule(String ruleId) {
        if (ruleActorRefList.containsKey(ruleId)) return;

        Rule rule = this.dataStorage.get(ruleId);

        // 创建rule actor
        ActorRef actorRef = actorSystem.actorOf((RuleActor.props(rule)), ruleId + "-" + IdUtil.getId(5));
        ruleActorRefList.put(ruleId, actorRef);
    }

    @Override
    public void stopRule(String ruleId) {
        if (!ruleActorRefList.containsKey(ruleId)) return;

        ActorRef ruleActorRef = ruleActorRefList.get(ruleId);
        actorSystem.stop(ruleActorRef);  // 停止rule actor
        ruleActorRefList.remove(ruleId);
        runtimeService.resetRuleRuntimeStatus(ruleId);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String dataKey() {
        return "rules";
    }

    @Override
    public List<Rule> createBackup() {
        return this.dataStorage.getValues();
    }

    @Override
    public void recoverBackup(List<Rule> dataList) {
        List<Rule> list = this.list(new Rule());
        for (Rule rule : list) {
            this.remove(rule.getRuleId());
        }
        for (Rule rule : dataList) {
            this.add(rule);
        }
    }

    //连接合法性校验
    private List<String> checkAndSupplement(Rule rule) {
        String graphJson = rule.getGraphJson();
        if (StringUtils.isEmpty(graphJson)) throw new DataValidateException("配置为空");

        Graph graph = JacksonUtils.toObj(graphJson, Graph.class);
        List<GraphNode> cells = graph.getCells();
        if (CollectionUtil.isEmpty(cells)) throw new DataValidateException("配置为空");

        List<GraphNode> nodeList = cells.stream().filter(cell -> !cell.getShape().equals("edge")).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(nodeList)) throw new DataValidateException("至少有一个节点");

        List<GraphNode> edgeList = cells.stream().filter(cell -> cell.getShape().equals("edge")).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(edgeList)) throw new DataValidateException("至少有一条连线");

        nodeList.forEach(node -> {
            NodeData data = node.getData();
            if (data == null) {
                throw new DataValidateException("节点未配置");
            }
            if (data.getProperties() == null || data.getProperties().isEmpty()) {
                throw new DataValidateException("节点[" + data.getName() + "]未配置");
            }
        });

        edgeList.forEach(edge -> {

            NodeLink source = edge.getSource();
            NodeLink target = edge.getTarget();

            Optional<GraphNode> sourceNode = nodeList.stream().filter(node -> node.getId().equals(source.getCell())).findFirst();
            if (!sourceNode.isPresent()) {
                throw new DataValidateException("连线起点不可为空");
            }

            Optional<GraphNode> targetNode = nodeList.stream().filter(node -> node.getId().equals(target.getCell())).findFirst();
            if (!targetNode.isPresent()) {
                throw new DataValidateException("连线终点不可为空");
            }

            Optional<NodePort> sourcePort = sourceNode.get().getPorts().getItems().stream().filter(port -> source.getPort().equals(port.getId())).findFirst();
            if (!sourcePort.isPresent()) {
                throw new DataValidateException("连线起点不存在");
            }

            Optional<NodePort> targetPort = targetNode.get().getPorts().getItems().stream().filter(port -> target.getPort().equals(port.getId())).findFirst();
            if (!targetPort.isPresent()) {
                throw new DataValidateException("连线终点不存在");
            }

            if (sourcePort.get().getGroup().equals("in")) {
                throw new DataValidateException("连线起点不可为[" + sourceNode.get().getData().getName() + "]的输入点");
            }

            if (targetPort.get().getGroup().equals("out")) {
                throw new DataValidateException("连线终点不可为[" + sourceNode.get().getData().getName() + "]的输出点");
            }
        });

        return nodeList.stream().map(GraphNode::getId).collect(Collectors.toList());
    }

}
