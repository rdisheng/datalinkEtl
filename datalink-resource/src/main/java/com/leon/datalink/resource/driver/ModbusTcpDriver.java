package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModbusTcpDriver extends AbstractDriver {

    private PlcConnection plcConnection;

    @Override
    public void create(Consumer<Object> output) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("salve"))) throw new DataValidateException();
        if (StringUtils.isEmpty(properties.getString("timeout"))) throw new DataValidateException();
        plcConnection = new PlcDriverManager().getConnection(String.format("modbus-tcp:tcp://%s:%s?unit-identifier=%s&request-timeout=%s",
                properties.getString("ip"),
                properties.getString("port"),
                properties.getString("salve"),
                properties.getString("timeout")));
    }

    @Override
    public void destroy() throws Exception {
        plcConnection.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getString("port"))) return false;
        if (StringUtils.isEmpty(properties.getString("salve"))) return false;
        try {
            PlcConnection plcConnection = new PlcDriverManager().getConnection(String.format("modbus-tcp:tcp://%s:%s?unit-identifier=%s",
                    properties.getString("ip"),
                    properties.getString("port"),
                    properties.getString("salve")));
            return plcConnection.isConnected();
        } catch (PlcConnectionException e) {
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

    //todo data处理
    private void read(Object data,Consumer<Object> output) throws Exception {
        List<Map<String, Object>> points = properties.getList("points");
        if (null == points || points.isEmpty()) throw new DataValidateException();

        try {
            if (!plcConnection.isConnected()) {
                plcConnection.connect();
            }
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            points.forEach(point -> builder.addItem((String) point.get("tag"), (String) point.get("address")));

            PlcReadResponse response = builder.build().execute().get();
            long timestamp = System.currentTimeMillis();
            List<Map<String, Object>> resultList = response.getFieldNames().stream().map(tag -> {
                Map<String, Object> result = new HashMap<>();
                boolean success = PlcResponseCode.OK.equals(response.getResponseCode(tag));
                result.put("tag", tag);
                result.put("success", success);
                result.put("value", success ? response.getObject(tag) : null);
                result.put("timestamp", timestamp);
                return result;
            }).collect(Collectors.toList());
            String transferType = properties.getString("transferType", "single");
            if ("single".equals(transferType)) {
                resultList.forEach(item -> output.accept(item));
            } else {
                output.accept(resultList);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("modbus read error: {}", e.getMessage());
        }
    }

    private void write(Object data,Consumer<Object> output) throws Exception {
        String address = properties.getString("address");
        String dataValue = properties.getString("dataValue");
        if (StringUtils.isEmpty(address)) throw new DataValidateException();
        if (StringUtils.isEmpty(dataValue)) throw new DataValidateException();

        Map<String, Object> variable = GlobalVariableContent.getAllValueWith(data);

        address = ExpressionUtil.analysis(address, variable);
        dataValue = ExpressionUtil.analysis(dataValue, variable);

        if (!plcConnection.isConnected()) {
            plcConnection.connect();
        }
        PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
        builder.addItem("WRITE_TAG", address, dataValue);

        PlcWriteResponse response = builder.build().execute().get();

//        HashMap<String, Object> map = new HashMap<>();
//        map.put("address", address);
//        map.put("value", dataValue);
//        map.put("success", PlcResponseCode.OK.equals(response.getResponseCode("WRITE_TAG")));
//        return map;
    }


}
