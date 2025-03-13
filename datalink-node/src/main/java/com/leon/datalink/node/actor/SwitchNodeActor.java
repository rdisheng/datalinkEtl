package com.leon.datalink.node.actor;

import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;

import java.util.List;
import java.util.Map;


public class SwitchNodeActor extends AbstractNodeActor {

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        Map<String, Object> globalVar = GlobalVariableContent.getAllValueWith(data);
        List<String> conditions = properties.getStringList("conditions");
        for (int i = 0; i < conditions.size(); i++) {
            String result = ExpressionUtil.analysis("${" + conditions.get(i) + "}", globalVar);
            if (Boolean.parseBoolean(result)) {
                output.out(i + 1, data);
            }
        }
    }

}
