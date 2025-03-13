package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.util.CharsetUtil;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileWatchNodeActor extends AbstractNodeActor {

    private Tailer tailer;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        String path = properties.getString("path");
        if (null == path) throw new DataValidateException();

        Long delay = properties.getLong("delay", 1000);

        File destFile = new File(path);
        if (!FileUtil.isFile(destFile)) {
            return;
        }
        tailer = new Tailer(destFile, CharsetUtil.CHARSET_UTF_8, content -> {
            if (StringUtils.isEmpty(content)) return;
            Map<String, Object> result = new HashMap<>();
            result.put("file", destFile.getPath());
            result.put("content", content);
            output.out(result);
        }, 0, delay);
        tailer.start(true);
    }


    @Override
    public void destroy() throws Exception {
        if (null != tailer) {
            tailer.stop();
        }
    }

}
