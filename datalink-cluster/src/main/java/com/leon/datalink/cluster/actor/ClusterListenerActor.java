package com.leon.datalink.cluster.actor;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import com.leon.datalink.cluster.distributed.ConsistencyManager;
import com.leon.datalink.cluster.member.ClusterMemberManager;
import com.leon.datalink.core.utils.Loggers;

/**
 * 集群监听
 */
public class ClusterListenerActor extends AbstractActor {
    Cluster cluster = Cluster.get(getContext().getSystem());

    public static Props props() {
        return Props.create(ClusterListenerActor.class);
    }

    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, this::onMemberUp)
                .match(ClusterEvent.MemberRemoved.class, this::onMemberDown)
                .build();
    }

    private void onMemberUp(ClusterEvent.MemberUp msg) {
        Member member = msg.member();
        boolean isLocal = cluster.selfAddress().equals(member.address());
        ClusterMemberManager.up(member, isLocal);
        ConsistencyManager.onMemberUp();
        Loggers.CLUSTER.info("Member UP {} [{}]", member.address(), isLocal ? "local" : "remote");
    }

    private void onMemberDown(ClusterEvent.MemberRemoved msg) {
        Member member = msg.member();
        boolean isLocal = cluster.selfAddress().equals(member.address());
        ClusterMemberManager.down(member);
        Loggers.CLUSTER.info("Member Removed {}", member.address());
    }

}
