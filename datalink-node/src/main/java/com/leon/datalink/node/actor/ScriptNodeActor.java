package com.leon.datalink.node.actor;

import com.leon.datalink.core.utils.ScriptUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import org.springframework.util.StringUtils;

import javax.script.*;
import java.util.Map;


public class ScriptNodeActor extends AbstractNodeActor {

    private ScriptEngine scriptEngine;

    private String language;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        language = properties.getString("language");
        scriptEngine = new ScriptEngineManager().getEngineByName(language);
    }

    @Override
    public void destroy() throws Exception {
        scriptEngine = null;
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        String script = properties.getString("script");
        if (null == data) {
            return;
        }
        if (StringUtils.isEmpty(script)) {
            output.out(data);
            return;
        }
        // 获取并绑定自定义环境变量
        Bindings bind = scriptEngine.createBindings();

        Map<String, Object> globalVariable = GlobalVariableContent.getAllValue();
        for (String key : globalVariable.keySet()) {
            bind.put(key, globalVariable.get(key));
        }

        scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(script);
        Invocable jsInvoke = (Invocable) scriptEngine;
        Object scriptResult = jsInvoke.invokeFunction("transform", data);

        if ("javascript".equals(language)) {
            scriptResult = ScriptUtil.toJavaObject(scriptResult);
        }

        output.out(scriptResult);


    }


}
