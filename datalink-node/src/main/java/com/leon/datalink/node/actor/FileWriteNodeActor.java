package com.leon.datalink.node.actor;

import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.core.exception.impl.DataValidateException;
import cn.hutool.core.io.file.FileAppender;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileWriteNodeActor extends AbstractNodeActor {

    private final ConcurrentHashMap<String, FileAppender> appenderList = new ConcurrentHashMap<>();


    @Override
    public void create(NodeContext context,Output output) throws Exception {
        String path = properties.getString("path");
        if (null == path) throw new DataValidateException();
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // 路径模板解析
        String path = ExpressionUtil.analysis(properties.getString("path"), variable);

        FileAppender fileAppender = appenderList.get(path);
        if (null == fileAppender) {
            Integer buffer = properties.getInteger("buffer", 5);
            boolean lineMode = properties.getBoolean("lineMode", true);
            fileAppender = new FileAppender(new File(path), buffer, lineMode);
            appenderList.put(path, fileAppender);
        }

        // 模板解析
        String content = properties.getString("content");
        if (!StringUtils.isEmpty(content)) {
            content = ExpressionUtil.analysis(content, variable);
        } else {
            if (null != data) {
                content = JacksonUtils.toJson(data);
            }
        }
        fileAppender.append(content);
    }

    @Override
    public void destroy() throws Exception {
        if (CollectionUtil.isNotEmpty(appenderList)) {
            appenderList.forEach((key, value) -> value.flush());
        }
    }

}
