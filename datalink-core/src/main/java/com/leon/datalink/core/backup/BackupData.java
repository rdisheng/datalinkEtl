package com.leon.datalink.core.backup;

import java.util.List;

/**
 * 需备份数据
 * @param <T>
 */
public interface BackupData<T> {

    /**
     * backup data key
     */
    String dataKey();

    /**
     * on create backup
     */
    List<T> createBackup();

    /**
     * on recover backup
     */
    void recoverBackup(List<T> dataList);

}
