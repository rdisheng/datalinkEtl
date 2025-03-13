package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.monitor.ListenerContent;
import com.leon.datalink.core.monitor.ListenerTypeEnum;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.HexUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CoapServerNodeActor extends AbstractNodeActor {

    CoapServer coapServer;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        Integer port = properties.getInteger("port");
        String path = properties.getString("path");
        if (null == port) throw new DataValidateException();
        if (StringUtils.isEmpty(path)) throw new DataValidateException();

        String payloadType = properties.getString("payloadType", "hex");

        coapServer = new CoapServer(port);

        coapServer.add(new CoapResource(path) {
            @Override
            public void handleRequest(Exchange exchange) {
                CoapExchange coapExchange = new CoapExchange(exchange);

                Map<String, Object> result = new HashMap<>();
                result.put("method", coapExchange.getRequestCode().name());
                result.put("options", coapExchange.getRequestOptions());
                result.put("payload",
                        "hex".equals(payloadType) ?
                                HexUtil.bytesToHex(coapExchange.getRequestPayload()) : coapExchange.getRequestText()
                );
                // 响应体解析
                String response = properties.getString("response");
                if (!StringUtils.isEmpty(response)) {
                    response = ExpressionUtil.analysis(response, GlobalVariableContent.getAllValue());
                    if ("hex".equals(payloadType)) {
                        coapExchange.respond(CoAP.ResponseCode.CONTENT, HexUtil.hexToBytes(response));
                    } else {
                        coapExchange.respond(CoAP.ResponseCode.CONTENT, response);
                    }
                }
                result.put("response", response);
                output.out(result);
            }
        });

        coapServer.start();
        ListenerContent.add(port, ListenerTypeEnum.UDP, "CoAP server node port");
    }

    @Override
    public void destroy() throws Exception {
        if (null != coapServer) coapServer.destroy();
        Integer port = properties.getInteger("port");
        if (null == port) throw new DataValidateException();
        ListenerContent.remove(port);
    }


}
