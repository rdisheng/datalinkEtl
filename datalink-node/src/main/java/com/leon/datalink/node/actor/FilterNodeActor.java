package com.leon.datalink.node.actor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;

import java.util.Map;


public class FilterNodeActor extends AbstractNodeActor {

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        Map<String, Object> globalVar = GlobalVariableContent.getAllValue();
        if (JacksonUtils.canToMap(data)) {
            globalVar.putAll(JacksonUtils.convertValue(data, new TypeReference<Map<String, Object>>() {
            }));
        }
        String condition = properties.getString("condition");
        String analysis = ExpressionUtil.analysis("${" + condition + "}", globalVar);
        if (Boolean.parseBoolean(analysis)) {
            output.out(data);
        }
    }


}
