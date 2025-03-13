package com.leon.datalink.node.actor;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class CommandNodeActor extends AbstractNodeActor {

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        Map<String, Object> globalVar = GlobalVariableContent.getAllValue();
        if (JacksonUtils.canToMap(data)) {
            globalVar.putAll(JacksonUtils.convertValue(data, new TypeReference<Map<String, Object>>() {
            }));
        }
        String command = properties.getString("command");

        command = ExpressionUtil.analysis(command, globalVar);

        Map<String, String> result = new HashMap<>();
        result.put("command", command);
        result.put("result", execCommand(command));
        output.out(result);
    }


    private String execCommand(String command) {
        String result = "error";
        try {
            Process process = Runtime.getRuntime().exec(command);
            InputStream is;
            int wait = process.waitFor();
            if (wait == 0) {
                is = process.getInputStream();
            } else {
                is = process.getErrorStream();
            }
            result = IoUtil.read(is, Charset.forName("GBK"));
            is.close();
            process.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }


}
