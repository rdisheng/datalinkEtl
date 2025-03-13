package com.leon.datalink.node.graph;

public class GraphNode {

    private String id;

    private String shape;

    private NodePorts ports;

    private NodeData data;

    private NodeLink source;

    private NodeLink target;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public NodePorts getPorts() {
        return ports;
    }

    public void setPorts(NodePorts ports) {
        this.ports = ports;
    }

    public NodeData getData() {
        return data;
    }

    public void setData(NodeData data) {
        this.data = data;
    }

    public NodeLink getSource() {
        return source;
    }

    public void setSource(NodeLink source) {
        this.source = source;
    }

    public NodeLink getTarget() {
        return target;
    }

    public void setTarget(NodeLink target) {
        this.target = target;
    }

}
