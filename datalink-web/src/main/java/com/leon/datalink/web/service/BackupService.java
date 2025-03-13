package com.leon.datalink.web.service;

import com.leon.datalink.core.backup.Backup;
import com.leon.datalink.core.service.BaseService;

public interface BackupService extends BaseService<Backup> {

    byte[] download(String fileName);

    void upload(String fileName, byte[] file);

    void recover(String fileName);
}
