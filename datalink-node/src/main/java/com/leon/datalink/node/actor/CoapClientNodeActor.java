package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.HexUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CoapClientNodeActor extends AbstractNodeActor {

    CoapClient coapClient;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        String ip = properties.getString("ip");
        Integer port = properties.getInteger("port");
        String method = properties.getString("method");
        if (StringUtils.isEmpty(ip)) throw new DataValidateException();
        if (null == port) throw new DataValidateException();
        if (StringUtils.isEmpty(method)) throw new DataValidateException();
        coapClient = new CoapClient(ip + ":" + port);
    }

    @Override
    public void destroy() throws Exception {
        if (null != coapClient) {
            coapClient.shutdown();
        }
    }


    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        output.out(doRequest(data));
    }

    private Map<String, Object> doRequest(Object data) throws ConnectorException, IOException {
        String payloadType = properties.getString("payloadType", "hex");

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        // 路径模板解析
        String path = ExpressionUtil.analysis(properties.getString("path"), variable);

        String url = String.format("%s:%s/%s", properties.getString("ip"), properties.getInteger("port"), path);
        coapClient.setURI(url);

        // 请求体模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            payload = ExpressionUtil.analysis(payload, variable);
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        Request request = new Request(CoAP.Code.valueOf(properties.getString("method")));
        request.setType(CoAP.Type.CON);
        request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN); //todo
        if ("hex".equals(payloadType)) {
            request.setPayload(HexUtil.hexToBytes(payload));
        } else {
            request.setPayload(payload);
        }

        CoapResponse response = coapClient.advanced(request);
        Map<String, Object> result = new HashMap<>();
        result.put("method", response.getCode().name());
        result.put("options", response.getOptions());
        result.put("payload", payload);
        if ("hex".equals(payloadType)) {
            result.put("response", HexUtil.bytesToHex(response.getPayload()));
        } else {
            result.put("response", response.getResponseText());
        }
        return result;
    }

}
