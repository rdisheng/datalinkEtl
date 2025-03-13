package com.leon.datalink.core.serializer;

import akka.serialization.JSerializer;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.ProtostuffUtils;

public class ProtostuffSerializer extends JSerializer {

    @Override
    public int identifier() {
        return 199512;
    }

    @Override
    public boolean includeManifest() {
        return true;
    }

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        Object result = null;
        try {
            result = ProtostuffUtils.deserialize(bytes, manifest);
        } catch (Exception e) {
            Loggers.CORE.error("protostuff deserialize error {}", e.getMessage());
        }
        return result;
    }

    @Override
    public byte[] toBinary(Object o) {
        byte[] result = null;
        try {
            result = ProtostuffUtils.serialize(o);
        } catch (Exception e) {
            Loggers.CORE.error("protostuff serialize error {}", e.getMessage());
        }
        return result;
    }

}
