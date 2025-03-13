package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;


public class DelayNodeActor extends AbstractNodeActor {

    //todo 未使用模板则提前创建Duration 判断是否表达式
    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        String delay = properties.getString("delay");
        String delayUnit = properties.getString("delayUnit");

        if(StringUtils.isEmpty(delay) || StringUtils.isEmpty(delayUnit)) throw new DataValidateException();

        delay = ExpressionUtil.analysis(delay, GlobalVariableContent.getAllValueWith(data));

        getContext().system().scheduler().scheduleOnce(
                Duration.create(Long.parseLong(delay),  TimeUnit.valueOf(delayUnit)),
                () -> output.out(data),
                getContext().dispatcher()
        );
    }


}
