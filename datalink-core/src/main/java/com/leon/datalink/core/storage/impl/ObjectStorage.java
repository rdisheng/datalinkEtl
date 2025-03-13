package com.leon.datalink.core.storage.impl;


import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.utils.DiskUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.ProtostuffUtils;
import org.lmdbjava.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.ByteBuffer.allocateDirect;
import static org.lmdbjava.ByteBufferProxy.PROXY_OPTIMAL;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.Env.create;

/**
 * @ClassName ObjectStorage
 * @Description LMDB
 * @Author Leon
 * @Date 2023年7月31日16:52:52
 * @Version V1.0
 **/
public class ObjectStorage<T> implements DataStorage<T> {

    private static Env<ByteBuffer> env;

    private final Class<T> clazz;

    private final Dbi<ByteBuffer> db;

    public ObjectStorage(Class<T> clazz) throws IOException {
        synchronized (ObjectStorage.class) {
            if (env == null) {
                File path = new File(EnvUtil.getStoragePath());
                DiskUtils.forceMkdir(path);
                env = create(PROXY_OPTIMAL)
                        .setMaxDbs(20)
                        .setMaxReaders(40)
                        .open(path);
                Loggers.CORE.info("Create LMDB env:{}", path);
            }
        }
        this.clazz = clazz;
        this.db = env.openDbi(clazz.getSimpleName().toLowerCase(), MDB_CREATE);
    }

    @Override
    public T get(String key) {
        final Cursor<ByteBuffer> c;
        T value;
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            c = this.db.openCursor(txn);
            c.get(byteBufferKey(key), GetOp.MDB_SET_KEY);
            value = ProtostuffUtils.deserializeFromByteBuffer(c.val(), clazz);
            c.close();
        }
        return value;
    }

    @Override
    public void put(String key, T value) {
        try (Txn<ByteBuffer> txn = env.txnWrite()) {
            final Cursor<ByteBuffer> c = this.db.openCursor(txn);
            c.put(byteBufferKey(key), ProtostuffUtils.serializeToByteBuffer(value));
            c.close();
            txn.commit();
        }
    }

    @Override
    public void delete(String key) {
        try (Txn<ByteBuffer> txn = env.txnWrite()) {
            this.db.delete(txn, byteBufferKey(key));
            txn.commit();
        }
    }

    @Override
    public List<String> getKeys() {
        List<String> result = new ArrayList<>();
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            Cursor<ByteBuffer> cursor = this.db.openCursor(txn);
            while (cursor.next()) {
                ByteBuffer val = cursor.key();
                byte[] bytes = new byte[val.capacity()];
                val.get(bytes);
                result.add(new String(bytes, StandardCharsets.UTF_8));
            }
            cursor.close();
        }
        return result;
    }

    @Override
    public List<T> getValues() {
        List<T> result = new ArrayList<>();
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            Cursor<ByteBuffer> cursor = this.db.openCursor(txn);
            while (cursor.next()) {
                ByteBuffer val = cursor.val();
                byte[] bytes = new byte[val.capacity()];
                val.get(bytes);
                result.add(ProtostuffUtils.deserialize(bytes, clazz));
            }
            cursor.close();
        }
        return result;
    }

    @Override
    public long count() {
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            return this.db.stat(txn).entries;
        }
    }

    static ByteBuffer byteBufferKey(final String value) {
        byte[] val = value.getBytes(StandardCharsets.UTF_8);
        final ByteBuffer bb = allocateDirect(val.length);
        bb.put(val).flip();
        return bb;
    }

}
