package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ClassUtil;
import com.leon.datalink.core.backup.Backup;
import com.leon.datalink.core.backup.BackupData;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.FileStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.*;
import com.leon.datalink.web.service.BackupService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @ClassNameResourceManager
 * @Description
 * @Author Leon
 * @Date2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class BackupServiceImpl implements BackupService {

    private final DataStorage<Backup> dataStorage;

    private final DataStorage<byte[]> fileStorage;


    public BackupServiceImpl() throws Exception {
        this.dataStorage = new ObjectStorage<>(Backup.class);
        this.fileStorage = new FileStorage(EnvUtil.getBackupFilePath());
    }

    @Override
    public byte[] download(String fileName) {
        return this.fileStorage.get(fileName);
    }

    /**
     * 上传并使用备份
     *
     * @param backupName
     * @param bytes
     */
    @Override
    public void upload(String backupName, byte[] bytes) {
        Backup backup = new Backup();
        backup.setBackupId(SnowflakeIdWorker.getId());
        backup.setBackupName(backupName);
        backup.setCreateTime(DateUtil.formatDateTime(new Date()));
        this.dataStorage.put(backup.getBackupId(), backup);
        this.fileStorage.put(backupName, bytes);
        this.doRecover(bytes);
    }

    /**
     * 恢复备份
     *
     * @param fileName
     */
    @Override
    public void recover(String fileName) {
        byte[] bytes = this.fileStorage.get(fileName);
        this.doRecover(bytes);
    }

    // 恢复备份
    private void doRecover(byte[] bytes) {
        Map<String, Object> map = JacksonUtils.toMapObj(bytes, String.class, Object.class);
        String version = (String) map.get("version");
        if (!VersionUtils.version.equals(version)) return;
        Map<String, BackupData> backupDataMap = SpringBeanUtil.getApplicationContext().getBeansOfType(BackupData.class);
        backupDataMap.values().forEach(backupData -> {
            Object content = map.get(backupData.dataKey());
            if (null != content) {
                backupData.recoverBackup(JacksonUtils.toListObj(content, ClassUtil.getTypeArgument(backupData.getClass())));
            }
        });
    }


    @Override
    public Backup get(String backupId) {
        return this.dataStorage.get(backupId);
    }

    /**
     * 创建备份
     *
     * @param backup
     */
    @Override
    public void add(Backup backup) {
        Date date = new Date();
        String backupName = String.format("datalink_backup_%s.json", DateUtil.format(date, "yyyyMMdd_HHmmssSSS"));

        Map<String, Object> backupContent = new HashMap<>();
        backupContent.put("version", VersionUtils.version);
        Map<String, BackupData> backupDataMap = SpringBeanUtil.getApplicationContext().getBeansOfType(BackupData.class);
        backupDataMap.values().forEach(backupData -> backupContent.put(backupData.dataKey(), backupData.createBackup()));

        this.fileStorage.put(backupName, JacksonUtils.toJsonBytes(backupContent));

        backup.setBackupId(SnowflakeIdWorker.getId());
        backup.setBackupName(backupName);
        backup.setCreateTime(DateUtil.formatDateTime(date));
        this.dataStorage.put(backup.getBackupId(), backup);
    }

    @Override
    public void remove(String backupId) {
        Backup backup = this.get(backupId);
        this.dataStorage.delete(backupId);
        this.fileStorage.delete(backup.getBackupName());
    }

    @Override
    public void update(Backup backup) {
        this.dataStorage.put(backup.getBackupId(), backup);
    }

    @Override
    public List<Backup> list(Backup backup) {
        Stream<Backup> stream = this.dataStorage.getValues().stream();
        if (null != backup) {
            if (!StringUtils.isEmpty(backup.getBackupName())) {
                stream = stream.filter(s -> s.getBackupName().contains(backup.getBackupName()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getBackupId()))).collect(Collectors.toList()));
    }

    @Override
    public long getCount() {
        return this.dataStorage.count();
    }


}
