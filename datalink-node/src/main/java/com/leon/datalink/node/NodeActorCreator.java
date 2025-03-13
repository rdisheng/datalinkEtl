package com.leon.datalink.node;

import akka.japi.Creator;

public class NodeActorCreator  implements Creator<AbstractNodeActor> {

    private final Class<? extends  AbstractNodeActor> nodeClass;
    private final NodeActorCreateParam createParam;

    public NodeActorCreator(Class<? extends  AbstractNodeActor> nodeClass, NodeActorCreateParam createParam) {
        this.nodeClass = nodeClass;
        this.createParam = createParam;
    }

    @Override
    public AbstractNodeActor create() throws Exception {
        AbstractNodeActor driverNodeActor = nodeClass.getDeclaredConstructor().newInstance();
        driverNodeActor.setCreateParam(createParam);
        return driverNodeActor;
    }

}
