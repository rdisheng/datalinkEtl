package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.util.opcua.OpcUAClientConfig;
import com.leon.datalink.resource.util.opcua.OpcUAClientFactory;
import com.leon.datalink.resource.util.opcua.OpcUAPoolConfig;
import com.leon.datalink.resource.util.opcua.OpcUATemplate;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OpcUADriver extends AbstractDriver {

    private OpcUATemplate opcUATemplate;

    private final HashMap<String, NodeId> readPointMap = new HashMap<>();

    @Override
    public void create(Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new DataValidateException();

        OpcUAClientConfig opcUAClientConfig = new OpcUAClientConfig();
        opcUAClientConfig.setIp(properties.getString("ip"));
        opcUAClientConfig.setPort(properties.getInteger("port"));
        opcUAClientConfig.setUsername(properties.getString("username"));
        opcUAClientConfig.setPassword(properties.getString("password"));
        opcUAClientConfig.setConnectTimeout(properties.getInteger("connectTimeout", 5000));
        opcUAClientConfig.setRequestTimeout(properties.getInteger("requestTimeout", 60000));
        opcUAClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 5000));
        opcUAClientConfig.setKeepAliveTimeout(properties.getInteger("keepAliveTimeout", 5000));

        OpcUAPoolConfig opcUAPoolConfig = new OpcUAPoolConfig();
        opcUAPoolConfig.setMaxTotal(properties.getInteger("maxTotal", 8));
        opcUAPoolConfig.setMaxIdle(properties.getInteger("maxIdle", 8));
        opcUAPoolConfig.setMinIdle(properties.getInteger("minIdle", 4));

        opcUATemplate = new OpcUATemplate(new OpcUAClientFactory(opcUAClientConfig), opcUAPoolConfig);

        // 解析待读取点位
        if (DriverModeEnum.SOURCE.equals(driverMode)) {
            List<Map<String, Object>> points = properties.getList("points");
            if (null == points || points.isEmpty()) throw new DataValidateException();
            points.forEach(point -> readPointMap.put((String) point.get("tag"), parseToNode((String) point.get("address"))));
        }

    }

    @Override
    public void destroy() throws Exception {
        opcUATemplate.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getString("port"))) return false;
        try {
            OpcUAClientConfig opcUAClientConfig = new OpcUAClientConfig();
            opcUAClientConfig.setIp(properties.getString("ip"));
            opcUAClientConfig.setPort(properties.getInteger("port"));
            opcUAClientConfig.setUsername(properties.getString("username"));
            opcUAClientConfig.setPassword(properties.getString("password"));
            opcUAClientConfig.setConnectTimeout(properties.getInteger("connectTimeout", 5000));
            opcUAClientConfig.setRequestTimeout(properties.getInteger("requestTimeout", 60000));
            opcUAClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 5000));
            opcUAClientConfig.setKeepAliveTimeout(properties.getInteger("keepAliveTimeout", 5000));
            OpcUAClientFactory opcUAClientFactory = new OpcUAClientFactory(opcUAClientConfig);
            OpcUaClient opcUaClient = opcUAClientFactory.create();
            return opcUaClient.getSession().get() != null;
        } catch (Exception e) {
            Loggers.DRIVER.error("driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void handleData(Object data,Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            this.read(data,output);
        } else if (driverMode.equals(DriverModeEnum.TARGET)) {
            this.write(data,output);
        }
    }

    private void read(Object data,Consumer<Object> output) throws Exception{
        if (readPointMap.isEmpty()) return;
        try {
            Map<NodeId, DataValue> nodeIdObjectMap = opcUATemplate.readValues(new ArrayList<>(readPointMap.values()));
            List<HashMap<String, Object>> resultList = new ArrayList<>();
            readPointMap.forEach((tag, nodeId) -> {
                DataValue dataValue = nodeIdObjectMap.get(nodeId);
                HashMap<String, Object> resultItem = new HashMap<>();
                resultItem.put("tag", tag);
                resultItem.put("success", dataValue.getStatusCode() != null && dataValue.getStatusCode().isGood());
                resultItem.put("value", dataValue.getValue().getValue());
                resultItem.put("timestamp", dataValue.getServerTime() == null ? null : dataValue.getServerTime().getJavaTime());
                resultList.add(resultItem);
            });
            String transferType = properties.getString("transferType", "single");
            if ("single".equals(transferType)) {
                resultList.forEach(item -> output.accept(item));
            } else {
                output.accept(resultList);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("opcua read error {}", e.getMessage());
        }
    }

    private void write(Object data,Consumer<Object> output) throws Exception{
        String address = properties.getString("address");
        String dataValue = properties.getString("dataValue");
        if (StringUtils.isEmpty(address)) throw new DataValidateException();
        if (StringUtils.isEmpty(dataValue)) throw new DataValidateException();

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        address = ExpressionUtil.analysis(address, variable);
        dataValue = ExpressionUtil.analysis(dataValue, variable);

        StatusCode statusCode;
        try {
            NodeId nodeId = parseToNode(address);
            statusCode = opcUATemplate.writeValue(nodeId, convertToType(dataValue, address));
        } catch (Exception e) {
            Loggers.DRIVER.error("opcua write error {}", e.getMessage());
            throw e;
        }

//        HashMap<String, Object> map = new HashMap<>();
//        map.put("address", address);
//        map.put("value", dataValue);
//        map.put("success", statusCode.isGood());
//        return map;
    }


    // 解析点位地址
    private NodeId parseToNode(String address) {
        String[] elements = address.split(";");
        if (elements.length != 2 && elements.length != 3) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }

        // 解析ns
        int namespaceIndex;
        String[] nsPair = elements[0].split("=");
        if (nsPair.length != 2) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        if (!nsPair[0].equals("ns")) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        namespaceIndex = Integer.parseInt(nsPair[1].trim());

        // 解析identifier
        String[] identifierPair = elements[1].split("=");
        if (identifierPair.length != 2) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        if (identifierPair[0].equals("i")) {
            return new NodeId(namespaceIndex, Integer.parseInt(identifierPair[1].trim()));
        } else {
            return new NodeId(namespaceIndex, identifierPair[1].trim());
        }

    }

    // 值类型转换
    public static Object convertToType(String value, String address) {
        String[] elements = address.split(";");
        if (elements.length != 3) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        switch (elements[2]) {
            case "Int16":
            case "UInt16": {
                return Short.parseShort(value);
            }
            case "Int32":
            case "UInt32": {
                return Integer.parseInt(value);
            }
            case "Int64":
            case "UInt64": {
                return Long.parseLong(value);
            }
            case "Float": {
                return Float.parseFloat(value);
            }
            case "Double": {
                return Double.parseDouble(value);
            }
            case "Boolean": {
                return Boolean.parseBoolean(value);
            }
            case "String": {
                return String.valueOf(value);
            }
            default: {
                return value;
            }
        }
    }

}
