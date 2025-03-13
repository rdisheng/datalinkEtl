package com.leon.datalink.node.actor;

import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;

import java.util.ArrayList;
import java.util.List;


public class PackNodeActor extends AbstractNodeActor {

    private final List<Object> tempPack = new ArrayList<>();

    private Long dataCount;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        dataCount = properties.getLong("dataCount", 1);
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        tempPack.add(data);
        if (tempPack.size() >= dataCount) {
            output.out(new ArrayList<>(tempPack));
            tempPack.clear();
        }
    }


}
