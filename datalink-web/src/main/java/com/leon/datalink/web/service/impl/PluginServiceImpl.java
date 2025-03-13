package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.FileStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.plugin.Plugin;
import com.leon.datalink.web.service.PluginService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName PluginServiceImpl
 * @Description
 * @Author Leon
 * @Date 2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class PluginServiceImpl implements PluginService {

    private final DataStorage<Plugin> dataStorage;

    private final DataStorage<byte[]> fileStorage;

    public PluginServiceImpl() throws Exception {
        this.dataStorage = new ObjectStorage<>(Plugin.class);
        this.fileStorage = new FileStorage(EnvUtil.getPluginFilePath());
    }

    @Override
    public void upload(String fileName, byte[] file) {
        fileStorage.put(fileName, file);
    }

    @Override
    public byte[] download(String fileName) {
        return this.fileStorage.get(fileName);
    }

    @Override
    public Plugin get(String pluginId) {
        return this.dataStorage.get(pluginId);
    }

    @Override
    public void add(Plugin plugin) {
        if (StringUtils.isEmpty(plugin.getPluginId())) plugin.setPluginId(SnowflakeIdWorker.getId());
        this.dataStorage.put(plugin.getPluginId(), plugin);
    }

    @Override
    public void remove(String pluginId) {
        Plugin plugin = this.get(pluginId);
        this.dataStorage.delete(pluginId);
        this.fileStorage.delete(plugin.getPluginName());
    }

    @Override
    public void update(Plugin plugin) {
        Plugin old = this.get(plugin.getPluginId());
        if (!old.getFileName().equals(plugin.getFileName())) {
            fileStorage.delete(old.getFileName());
        }
        this.dataStorage.put(plugin.getPluginId(), plugin);
    }

    @Override
    public List<Plugin> list(Plugin plugin) {
        Stream<Plugin> stream = this.dataStorage.getValues().stream();
        if (null != plugin) {
            if (!StringUtils.isEmpty(plugin.getPluginName())) {
                stream = stream.filter(s -> s.getPluginName().contains(plugin.getPluginName()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getPluginId()))).collect(Collectors.toList()));
    }

    @Override
    public long getCount() {
        return this.dataStorage.count();
    }


}
