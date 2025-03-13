package com.leon.datalink.resource.driver;

import com.leon.datalink.core.exception.impl.DataValidateException;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * snmp v1 v2c   // todo v3
 */
public class SnmpDriver extends AbstractDriver {

    private Snmp snmp;

    private Target target;

    private ResponseListener listener;

    private List<String> getOidList;

    private List<String> walkOidList;

    @Override
    public void create(Consumer<Object> output) throws Exception {

        String url = getUrl(properties);
        if (StringUtils.isEmpty(url)) throw new DataValidateException();

        String community = properties.getString("community");
        if (StringUtils.isEmpty(community)) throw new DataValidateException();

        Integer version = properties.getInteger("version"); // 0 1 3
        if (version == null) throw new DataValidateException();

        MessageDispatcher messageDispatcher = new MessageDispatcherImpl();

        if (version == SnmpConstants.version1) messageDispatcher.addMessageProcessingModel(new MPv1());
        if (version == SnmpConstants.version2c) messageDispatcher.addMessageProcessingModel(new MPv2c());
        snmp = new Snmp(messageDispatcher, new DefaultUdpTransportMapping());
        snmp.listen();

        target = new CommunityTarget();
        target.setSecurityName(new OctetString(community));
        if (version == SnmpConstants.version2c) target.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv2c);
        target.setVersion(version);
        target.setAddress(GenericAddress.parse(url));
        target.setRetries(properties.getInteger("retries", 3));
        target.setTimeout(properties.getLong("timeout", 3000));

        listener = new ResponseListener() {
            public void onResponse(ResponseEvent event) {
                ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                PDU response = event.getResponse();
                if (response == null || response.getErrorStatus() != 0) {
                    Loggers.DRIVER.error("snmp driver error {}", response);
                } else {
                    long timestamp = System.currentTimeMillis();
                    List<Map<String, Object>> resultList = Arrays.stream(response.toArray()).map(vb -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("oid", vb.getOid().toString());
                        result.put("success", null != vb.getVariable());
                        result.put("value", vb.getVariable().toString());
                        result.put("timestamp", timestamp);
                        return result;
                    }).collect(Collectors.toList());
                    String transferType = properties.getString("transferType", "single");
                    if ("single".equals(transferType)) {
                        resultList.forEach(result -> output.accept(result));
                    } else {
                        output.accept(resultList);
                    }
                }
            }
        };

        List<Map<String, Object>> points = properties.getList("points");
        if (null == points || points.isEmpty()) throw new DataValidateException();

        getOidList = points.stream().filter(point -> point.get("type").equals("GET")).map(point -> (String) point.get("oid")).collect(Collectors.toList());
        walkOidList = points.stream().filter(point -> point.get("type").equals("WALK")).map(point -> (String) point.get("oid")).collect(Collectors.toList());
    }

    private String getUrl(ConfigProperties properties) {
        String ip = properties.getString("ip");
        Integer port = properties.getInteger("port");
        if (StringUtils.isEmpty(ip) || null == port) throw new DataValidateException();
        return String.format("udp:%s/%s", ip, port);
    }

    public void sendSnmp(int type, List<String> oidList) throws Exception {
        if (null == oidList || oidList.isEmpty()) return;
        try {
            PDU pdu = new PDU();
            pdu.setType(type);
            for (String oid : oidList) {
                pdu.add(new VariableBinding(new OID(oid)));
            }
            this.snmp.send(pdu, target, null, listener);
        } catch (Exception e) {
            Loggers.DRIVER.error("snmp driver error {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public void destroy() throws Exception {
        if (null != snmp) snmp.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        Integer port = properties.getInteger("port");
        if (null == port) return false;
        return NetUtil.isValidPort(port);
    }

    @Override
    public void handleData(Object data,Consumer<Object> output) throws Exception {
        if (driverMode.equals(DriverModeEnum.TARGET)) {
            throw new UnsupportedOperationException();
        }
        sendSnmp(PDU.GET, getOidList);
        sendSnmp(PDU.GETNEXT, walkOidList);
    }
}
