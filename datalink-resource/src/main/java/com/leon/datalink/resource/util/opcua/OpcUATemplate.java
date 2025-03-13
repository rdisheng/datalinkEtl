package com.leon.datalink.resource.util.opcua;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpcUATemplate extends GenericObjectPool<OpcUaClient> {

    public OpcUATemplate(PooledObjectFactory<OpcUaClient> factory, GenericObjectPoolConfig<OpcUaClient> config) {
        super(factory, config);
    }

    public DataValue readValue(NodeId nodeId) throws Exception {
        OpcUaClient client = null;
        try {
            client = super.borrowObject();
            return client.readValue(0.0, TimestampsToReturn.Both, nodeId).get();
        } finally {
            if (client != null) {
                super.returnObject(client);
            }
        }
    }

    public Map<NodeId, DataValue> readValues(List<NodeId> nodeIds) throws Exception {
        OpcUaClient client = null;
        try {
            client = super.borrowObject();
            List<DataValue> dataValues = client.readValues(0.0, TimestampsToReturn.Both, nodeIds).get();
            Map<NodeId, DataValue> result = new HashMap<>();
            if (dataValues.size() != nodeIds.size()) return result;
            for (int i = 0; i < nodeIds.size(); i++) {
                result.put(nodeIds.get(i), dataValues.get(i));
            }
            return result;
        } finally {
            if (client != null) {
                super.returnObject(client);
            }
        }
    }

    public StatusCode writeValue(NodeId nodeId, Object value) throws Exception {
        OpcUaClient client = null;
        try {
            client = super.borrowObject();
            return client.writeValue(nodeId, new DataValue(new Variant(value), null, null)).get();
        } finally {
            if (client != null) {
                super.returnObject(client);
            }
        }
    }


}
