
package com.leon.datalink.core.storage.impl;

import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.utils.DiskUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName FileStorage
 * @Description
 * @Author Leon
 * @Date 2023年7月29日21:29:51
 * @Version V1.0
 **/
public class FileStorage implements DataStorage<byte[]> {

    private final String baseDir;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public FileStorage(String baseDir) throws IOException {
        this.baseDir = baseDir;
        DiskUtils.forceMkdir(baseDir);
    }

    @Override
    public byte[] get(String fileName) {
        readLock.lock();
        try {
            File file = Paths.get(baseDir, fileName).toFile();
            if (file.exists()) {
                return DiskUtils.readFileBytes(file);
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(String fileName, byte[] value) {
        writeLock.lock();
        try {
            File file = Paths.get(baseDir, fileName).toFile();
            try {
                DiskUtils.touch(file);
                DiskUtils.writeFile(file, value, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(String fileName) {
        readLock.lock();
        try {
            DiskUtils.deleteFile(baseDir, fileName);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<String> getKeys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<byte[]> getValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        return Objects.requireNonNull(new File(baseDir).listFiles()).length;
    }

}