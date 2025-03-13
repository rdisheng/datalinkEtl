package com.leon.datalink.resource.util.opcua;

import com.leon.datalink.core.utils.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class OpcUAClientFactory extends BasePooledObjectFactory<OpcUaClient> {

    private final OpcUAClientConfig opcUAClientConfig;

    public OpcUAClientFactory(OpcUAClientConfig opcUAClientConfig) {
        this.opcUAClientConfig = opcUAClientConfig;
    }

    @Override
    public OpcUaClient create() throws Exception {
        IdentityProvider identityProvider;
        if (StringUtils.isNotEmpty(opcUAClientConfig.getUsername()) && StringUtils.isNotEmpty(opcUAClientConfig.getPassword())) {
            identityProvider = new UsernameProvider(opcUAClientConfig.getUsername(), opcUAClientConfig.getPassword());
        } else {
            identityProvider = new AnonymousProvider();
        }
        OpcUaClient opcUaClient = OpcUaClient.create(
                String.format("opc.tcp://%s:%s", opcUAClientConfig.getIp(), opcUAClientConfig.getPort()),
                endpoints -> endpoints.stream().findFirst(),
                configBuilder -> configBuilder
                        .setIdentityProvider(identityProvider)
                        .setConnectTimeout(uint(opcUAClientConfig.getConnectTimeout()))
                        .setRequestTimeout(uint(opcUAClientConfig.getRequestTimeout()))
                        .setKeepAliveInterval(uint(opcUAClientConfig.getKeepAliveInterval()))
                        .setKeepAliveTimeout(uint(opcUAClientConfig.getKeepAliveTimeout()))
                        .build()
        );
        return (OpcUaClient) opcUaClient.connect().get();
    }

    @Override
    public PooledObject<OpcUaClient> wrap(OpcUaClient opcUaClient) {
        return new DefaultPooledObject<>(opcUaClient);
    }

    @Override
    public void destroyObject(PooledObject<OpcUaClient> pooledOpcUAClient) {
        if (pooledOpcUAClient != null && pooledOpcUAClient.getObject() != null) {
            pooledOpcUAClient.getObject().disconnect();
        }
    }

    @Override
    public boolean validateObject(PooledObject<OpcUaClient> pooledOpcUAClient) {
        try {
            return pooledOpcUAClient.getObject().getSession().get() != null;
        } catch (Exception e) {
            return false;
        }
    }

}
