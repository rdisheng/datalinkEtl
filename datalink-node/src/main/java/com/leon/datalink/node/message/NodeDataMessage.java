package com.leon.datalink.node.message;

import com.leon.datalink.core.serializer.ProtostuffSerializable;
import com.leon.datalink.node.context.NodeContext;

public class NodeDataMessage implements ProtostuffSerializable {

    private NodeContext context;

    private Object data;

    public NodeDataMessage(Object data) {
        this.data = data;
    }

    public NodeDataMessage(NodeContext context, Object data) {
        this.context = context;
        this.data = data;
    }

    public NodeContext getContext() {
        return context;
    }

    public void setContext(NodeContext context) {
        this.context = context;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
