package com.leon.datalink.node.actor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.monitor.ListenerContent;
import com.leon.datalink.core.monitor.ListenerTypeEnum;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import com.leon.datalink.node.response.ResponseManager;
import com.leon.datalink.resource.util.http.SimpleHttpServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServerNodeActor extends AbstractNodeActor {

    SimpleHttpServer simpleHttpServer;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        Integer port = properties.getInteger("port");
        List<String> paths = properties.getStringList("paths");
        if (null == port) throw new DataValidateException();
        if (CollectionUtil.isEmpty(paths)) throw new DataValidateException();
        simpleHttpServer = new SimpleHttpServer(port);

        int index = 1;
        for (String path : paths) {
            final int portIndex = index++;
            simpleHttpServer.addHandler(path, httpExchange -> {
                HttpServerRequest req = new HttpServerRequest(httpExchange);

                String requestId = SnowflakeIdWorker.getId();
                ResponseManager.add(requestId, httpExchange);

                context.put("requestId", requestId);

                Map<String, Object> result = new HashMap<>();
                result.put("path", req.getPath());
                result.put("method", req.getMethod());
                result.put("headers", req.getHeaders());
                result.put("body", req.getBody());
                result.put("params", req.getParams());
                output.out(portIndex, result);
            });
        }

        simpleHttpServer.start();
        ListenerContent.add(port, ListenerTypeEnum.TCP, "HTTP server node port");
    }

    @Override
    public void destroy() throws Exception {
        if (null != simpleHttpServer) simpleHttpServer.stop(0);
        Integer port = properties.getInteger("port");
        if (null == port) throw new DataValidateException();
        ListenerContent.remove(port);
    }


}
