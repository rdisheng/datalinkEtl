package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.core.backup.BackupData;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.DriverFactory;
import com.leon.datalink.resource.entity.Resource;
import com.leon.datalink.web.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName ResourceServiceImpl
 * @Description
 * @Author Leon
 * @Date 2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class ResourceServiceImpl implements ResourceService, BackupData<Resource> {

    /**
     * key value storage
     */
    private final ObjectStorage<Resource> kvStorage;

    public ResourceServiceImpl() throws Exception {
        this.kvStorage = new ObjectStorage<>(Resource.class);
    }

    @Override
    public Resource get(String resourceId) {
        return this.kvStorage.get(resourceId);
    }

    @Override
    public void add(Resource resource) {
        if (StringUtils.isEmpty(resource.getResourceId())) resource.setResourceId(SnowflakeIdWorker.getId());
        this.kvStorage.put(resource.getResourceId(), resource);
    }

    @Override
    public void remove(String resourceId) {
        this.kvStorage.delete(resourceId);
    }

    @Override
    public void update(Resource resource) {
        this.kvStorage.put(resource.getResourceId(), resource);
    }

    @Override
    public List<Resource> list(Resource resource) {
        Stream<Resource> stream = this.kvStorage.getValues().stream();
        if (null != resource.getResourceType()) {
            stream = stream.filter(r -> r.getResourceType().equals(resource.getResourceType()));
        }
        if (!StringUtils.isEmpty(resource.getResourceName())) {
            stream = stream.filter(r -> r.getResourceName().contains(resource.getResourceName()));
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getResourceId()))).collect(Collectors.toList()));
    }

    @Override
    public long getCount() {
        return this.kvStorage.count();
    }

    @Override
    public boolean testDriver(Resource resource) throws Exception {
        Driver driver = DriverFactory.getDriver(resource.getResourceType().getDriver());
        return driver.test(resource.getProperties());
    }

    @Override
    public String dataKey() {
        return "resources";
    }

    @Override
    public List<Resource> createBackup() {
        return this.kvStorage.getValues();
    }

    @Override
    public void recoverBackup(List<Resource> dataList) {
        List<Resource> list = this.list(new Resource());
        for (Resource resource : list) {
            this.remove(resource.getResourceId());
        }
        for (Resource resource : dataList) {
            this.add(resource);
        }
    }
}
