package com.leon.datalink.core.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.ByteBuffer.allocateDirect;

public class ProtostuffUtils {
    /**
     * 避免每次序列化都重新申请Buffer空间
     */
    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    /**
     * 缓存Schema
     */
    private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    /**
     * 序列化方法，把指定对象序列化成字节数组
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }

        return data;
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


    /**
     * 序列化方法，把指定对象序列化成ByteBuffer
     */
    public static <T> ByteBuffer serializeToByteBuffer(T obj) {
        byte[] bytes = serialize(obj);
        final ByteBuffer byteBuffer = allocateDirect(bytes.length);
        byteBuffer.put(bytes).flip();
        return byteBuffer;
    }

    /**
     * 反序列化方法，将ByteBuffer反序列化成指定Class类型
     */
    public static <T> T deserializeFromByteBuffer(ByteBuffer data, Class<T> clazz) {
        byte[] bytes = new byte[data.capacity()];
        data.get(bytes);
        return deserialize(bytes, clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            //这个schema通过RuntimeSchema进行懒创建并缓存
            //所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }

        return schema;
    }
}
