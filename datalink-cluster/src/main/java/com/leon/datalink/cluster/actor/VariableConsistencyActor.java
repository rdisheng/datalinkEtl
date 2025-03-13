package com.leon.datalink.cluster.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ddata.*;
import akka.cluster.ddata.Replicator.Changed;
import akka.cluster.ddata.Replicator.Subscribe;
import akka.cluster.ddata.Replicator.Update;
import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.cluster.distributed.ConsistencyCommandEnum;
import com.leon.datalink.cluster.distributed.ConsistencyWrapper;
import com.leon.datalink.cluster.member.ClusterMemberManager;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.core.variable.Variable;

import java.util.List;


/**
 * global variable distributed consistency
 *
 * @author liyang
 */
public class VariableConsistencyActor extends AbstractActor {

    private final ActorRef replicator = DistributedData.get(getContext().getSystem()).replicator();

    private final Cluster node = Cluster.get(getContext().getSystem());

    private final Key<ORSet<Variable>> variableKey = ORSetKey.create("variableKey");

    @Override
    public void preStart() {
        Loggers.CLUSTER.info("start variable consistency actor {}", getSelf().path());
        Subscribe<ORSet<Variable>> subscribe = new Subscribe<>(variableKey, getSelf());
        replicator.tell(subscribe, ActorRef.noSender());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ConsistencyWrapper.class, consistencyWrapper -> consistencyWrapper.getCommand().equals(ConsistencyCommandEnum.INIT), this::init)
                .match(ConsistencyWrapper.class, consistencyWrapper -> consistencyWrapper.getCommand().equals(ConsistencyCommandEnum.ADD), this::add)
                .match(ConsistencyWrapper.class, consistencyWrapper -> consistencyWrapper.getCommand().equals(ConsistencyCommandEnum.DELETE), this::delete)
                .match(ConsistencyWrapper.class, consistencyWrapper -> consistencyWrapper.getCommand().equals(ConsistencyCommandEnum.DESTROY), this::destroy)
                .match(Changed.class, changed -> changed.key().equals(variableKey), changed -> receiveChanged((Changed<ORSet<Variable>>) changed))
                .build();
    }

    /**
     * 初始同步
     *
     * @param wrapper
     */
    private void init(ConsistencyWrapper wrapper) {
        List<Variable> customVariables = GlobalVariableContent.getCustomVariables();
        if (CollectionUtil.isEmpty(customVariables)) return;
        Update<ORSet<Variable>> update = new Update<>(variableKey, ORSet.create(), Replicator.writeLocal(), variableORSet -> {
            for (Variable variable : customVariables) {
                variableORSet = variableORSet.add(node.selfUniqueAddress(), variable.setMemberName(ClusterMemberManager.getLocalMemberName()));
            }
            return variableORSet;
        });
        replicator.tell(update, getSelf());
        Loggers.CLUSTER.info("variable consistency actor init data");
    }

    /**
     * 新增同步
     *
     * @param wrapper
     */
    private void add(ConsistencyWrapper wrapper) {
        Variable variable = (Variable) wrapper.getData();
        variable.setMemberName(ClusterMemberManager.getLocalMemberName());
        Update<ORSet<Variable>> update = new Update<>(variableKey, ORSet.create(), Replicator.writeLocal(), curr -> curr.add(node.selfUniqueAddress(),variable));
        replicator.tell(update, getSelf());
        Loggers.CLUSTER.info("variable consistency actor add data");
    }

    /**
     * 删除同步
     *
     * @param wrapper
     */
    private void delete(ConsistencyWrapper wrapper) {
        Variable variable = (Variable) wrapper.getData();
        variable.setMemberName(ClusterMemberManager.getLocalMemberName());
        Update<ORSet<Variable>> delete = new Update<>(variableKey, ORSet.create(), Replicator.writeLocal(), curr -> curr.remove(node.selfUniqueAddress(), variable));
        replicator.tell(delete, getSelf());
        Loggers.CLUSTER.info("variable consistency actor delete data");
    }

    /**
     * 销毁同步
     *
     * @param wrapper
     */
    private void destroy(ConsistencyWrapper wrapper) {
        String localMemberName = ClusterMemberManager.getLocalMemberName();
        List<Variable> localCustomVariables = GlobalVariableContent.getMemberCustomVariables(localMemberName);
        if (CollectionUtil.isEmpty(localCustomVariables)) return;
        Update<ORSet<Variable>> update = new Update<>(variableKey, ORSet.create(), Replicator.writeLocal(), variableORSet -> {
            for (Variable variable : localCustomVariables) {
                variableORSet = variableORSet.remove(node.selfUniqueAddress(), variable.setMemberName(localMemberName));
            }
            return variableORSet;
        });
        replicator.tell(update, getSelf());
        Loggers.CLUSTER.info("variable consistency actor destroy data");
    }

    private void receiveChanged(Changed<ORSet<Variable>> changed) {
        ORSet<Variable> data = changed.dataValue();
        GlobalVariableContent.setAllCustomVariables(data.getElements());
        Loggers.CLUSTER.info("variable consistency actor changed data");
    }

}