package com.leon.datalink.node.actor;

import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import com.leon.datalink.node.schedule.ScheduleManager;
import com.leon.datalink.node.schedule.ScheduleParam;


public class ScheduleNodeActor extends AbstractNodeActor {

    private String payload;

    private String payloadFormat;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        payload = properties.getString("payload", "");
        payloadFormat = properties.getString("payloadFormat", "json");

        ScheduleParam scheduleParam = new ScheduleParam();
        scheduleParam.setNodeId(this.nodeId);
        scheduleParam.setInitialDelay(properties.getLong("initialDelay"));
        scheduleParam.setInitialDelayUnit(properties.getString("initialDelayUnit"));
        scheduleParam.setCronExpression(properties.getString("cronExpression"));
        ScheduleManager.create(getSelf(), scheduleParam);
    }

    @Override
    public void destroy() throws Exception {
        ScheduleManager.stop(this.nodeId);
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        String result = ExpressionUtil.analysis(payload, GlobalVariableContent.getAllValue());
        if (payloadFormat.equals("json")) {
            output.out(StringUtils.isEmpty(result) ? result : JacksonUtils.toObj(result));
        } else {
            output.out(result);
        }
    }


}
