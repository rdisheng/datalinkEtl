package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;


public class TemplateNodeActor extends AbstractNodeActor {

    private String template;

    private String outputFormat;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        template = properties.getString("template");
        if (StringUtils.isEmpty(template)) throw new DataValidateException();
        outputFormat = properties.getString("outputFormat", "json");
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        String result = ExpressionUtil.analysis(template, GlobalVariableContent.getAllValueWith(data));
        if (outputFormat.equals("json")) {
            output.out(JacksonUtils.toObj(result));
        } else {
            output.out(result);
        }
    }


}
