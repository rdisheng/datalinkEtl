package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import cn.hutool.http.server.HttpServerResponse;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import com.leon.datalink.node.response.ResponseManager;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class HttpResponseNodeActor extends AbstractNodeActor {

    private String contentType;
    private String contentTemplate;
    private Map<String, String> headersMap;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        contentType = properties.getString("contentType");
        if (StringUtils.isEmpty(contentType)) {
            throw new DataValidateException();
        }
        contentTemplate = properties.getString("content", "");
        headersMap = properties.getMap("headers");
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        String requestId = context.getString("requestId");
        if (StringUtils.isEmpty(requestId)) throw new DataValidateException();

        Map<String, Object> globalVar = GlobalVariableContent.getAllValueWith(data);
        String content = StringUtils.isEmpty(contentTemplate) ?
                JacksonUtils.toJson(data) :
                ExpressionUtil.analysis(contentTemplate, globalVar);
        HttpExchange httpExchange = (HttpExchange) ResponseManager.get(requestId);
        HttpServerResponse httpServerResponse = new HttpServerResponse(httpExchange);
        if (headersMap != null) {
            headersMap.forEach(httpServerResponse::addHeader);
        }
        httpServerResponse.write(content, contentType);
        httpExchange.close();
    }

}
