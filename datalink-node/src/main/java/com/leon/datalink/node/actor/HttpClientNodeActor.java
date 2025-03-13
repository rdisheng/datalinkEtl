package com.leon.datalink.node.actor;

import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClientNodeActor extends AbstractNodeActor {

    private HttpComponentsClientHttpRequestFactory requestFactory;

    private RestTemplate restTemplate;

    @Override
    public void create(NodeContext context, Output output) throws Exception {

        this.requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getInteger("connectTimeout", 6000));
        requestFactory.setReadTimeout(properties.getInteger("readTimeout", 6000));

        this.restTemplate = new RestTemplate(this.requestFactory);
    }

    @Override
    public void destroy() throws Exception {
        requestFactory.destroy();
    }


    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        output.out(doRequest(data));
    }

    private Map<String, Object> doRequest(Object data) {

        String url = properties.getString("url");
        String method = properties.getString("method");
        if (StringUtils.isEmpty(url)) return null;
        if (StringUtils.isEmpty(method)) return null;

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // 路径模板解析
        url = ExpressionUtil.analysis(url, variable);

        // 请求体模板解析
        String body = properties.getString("body");
        if (!StringUtils.isEmpty(body)) {
            body = ExpressionUtil.analysis(body, variable);
        } else {
            if (null != data) {
                body = JacksonUtils.toJson(data);
            }
        }

        Map<String, String> headersMap = properties.getMap("headers");

        final String param = body;
        RequestCallback requestCallback = request -> {
            if (null != headersMap) request.getHeaders().setAll(headersMap);
            if (!StringUtils.isEmpty(param)) request.getBody().write(param.getBytes(StandardCharsets.UTF_8));
        };

        String response = restTemplate.execute(url, HttpMethod.valueOf(method), requestCallback, clientHttpResponse -> {
            InputStream in = clientHttpResponse.getBody();
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            return out.toString();
        });


        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("param", param);
        result.put("response", response);
        return result;
    }

}
