package com.leon.datalink.node;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import cn.hutool.core.util.ObjectUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import com.leon.datalink.node.message.NodeDataMessage;
import com.leon.datalink.node.message.NodeOutRefGetMessage;
import com.leon.datalink.runtime.message.RuntimeData;
import com.leon.datalink.runtime.message.RuntimeStatus;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public abstract class AbstractNodeActor extends AbstractActor {

    protected String nodeId;

    private ActorRef ruleRef;

    private Map<Integer, List<ActorRef>> outRef;

    protected ConfigProperties properties;

    @Override
    public void preStart() {
        try {
            NodeContext context = new NodeContext();
            this.create(context, new Output() {
                @Override
                public void out(Object data) {
                    output(context, 1, data);
                }

                @Override
                public void out(Integer portIndex, Object data) {
                    output(context, portIndex, data);
                }
            });
            Loggers.NODE.info("start node [{}]", nodeId);
            ruleRef.tell(RuntimeStatus.normal(nodeId), getSelf());
        } catch (Exception e) {
            ruleRef.tell(RuntimeStatus.abnormal(nodeId), getSelf());
            ruleRef.tell(RuntimeData.exception(nodeId, e.getMessage()), getSelf());
            Loggers.NODE.error("start node [{}] error", nodeId, e);
        }
    }

    @Override
    public final void postStop() {
        try {
            this.destroy();
            Loggers.NODE.info("stop node [{}]", nodeId);
        } catch (Exception e) {
            ruleRef.tell(RuntimeStatus.abnormal(nodeId), getSelf());
            ruleRef.tell(RuntimeData.exception(nodeId, e.getMessage()), getSelf());
            Loggers.NODE.error("stop node [{}] error", nodeId, e);
        }
    }

    @Override
    public final Receive createReceive() {
        return receiveBuilder()
                .match(NodeDataMessage.class, this::dataMessageHandle)
                .build();
    }

    protected final void setCreateParam(NodeActorCreateParam nodeActorCreateParam) {
        this.nodeId = nodeActorCreateParam.getNodeId();
        this.ruleRef = nodeActorCreateParam.getRuleRef();
        this.properties = nodeActorCreateParam.getProperties();
    }

    private void dataMessageHandle(NodeDataMessage nodeDataMessage) {
        try {
            Object data = nodeDataMessage.getData();
            NodeContext context = ObjectUtil.clone(nodeDataMessage.getContext());
            this.onInput(context, ObjectUtil.clone(data), new Output() {
                @Override
                public void out(Object data) {
                    output(context, 1, data);
                }

                @Override
                public void out(Integer portIndex, Object data) {
                    output(context, portIndex, data);
                }
            });
            ruleRef.tell(RuntimeData.input(nodeId, data), getSelf());
        } catch (Exception e) {
            ruleRef.tell(RuntimeData.exception(nodeId, e.getMessage()), getSelf());
        }
    }

    private void output(NodeContext context, Integer portIndex, Object data) {
        if (outRef == null) {
            try {
                Timeout timeout = Timeout.create(Duration.ofSeconds(5));
                Future<Object> response = Patterns.ask(ruleRef, new NodeOutRefGetMessage(nodeId), timeout);
                NodeOutRefGetMessage message = (NodeOutRefGetMessage) Await.result(response, timeout.duration());
                outRef = message.getOutRef();
            } catch (Exception e) {
                Loggers.NODE.error("node get out ref error", e);
            }
        }
        if (outRef != null) {
            List<ActorRef> actorRefs = outRef.get(portIndex);
            if (actorRefs != null) {
                actorRefs.forEach(actorRef -> actorRef.tell(new NodeDataMessage(context, data), getSelf()));
            }
        }
        ruleRef.tell(RuntimeData.output(nodeId, portIndex, data), getSelf());
    }

    public void create(NodeContext context, Output output) throws Exception {
    }

    public void destroy() throws Exception {
    }

    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        throw new UnsupportedOperationException();
    }

}
