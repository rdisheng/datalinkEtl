package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.script.Script;
import com.leon.datalink.web.service.ScriptService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName ScriptServiceImpl
 * @Description
 * @Author Leon
 * @Date 2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class ScriptServiceImpl implements ScriptService {

    private final DataStorage<Script> dataStorage;

    public ScriptServiceImpl() throws Exception {
        this.dataStorage = new ObjectStorage<>(Script.class);
    }

    @Override
    public Script get(String scriptId) {
        return this.dataStorage.get(scriptId);
    }

    @Override
    public void add(Script script) {
        if (StringUtils.isEmpty(script.getScriptId())) script.setScriptId(SnowflakeIdWorker.getId());
        this.dataStorage.put(script.getScriptId(), script);
    }

    @Override
    public void remove(String scriptId) {
        this.dataStorage.delete(scriptId);
    }

    @Override
    public void update(Script script) {
        this.dataStorage.put(script.getScriptId(), script);
    }

    @Override
    public List<Script> list(Script script) {
        Stream<Script> stream = this.dataStorage.getValues().stream()
                .peek(value -> value.setScriptContent(null));
        if (null != script) {
            if (!StringUtils.isEmpty(script.getScriptName())) {
                stream = stream.filter(s -> s.getScriptName().contains(script.getScriptName()));
            }
            if (!StringUtils.isEmpty(script.getScriptLanguage())) {
                stream = stream.filter(s -> s.getScriptLanguage().equals(script.getScriptLanguage()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getScriptId()))).collect(Collectors.toList()));
    }

    @Override
    public long getCount() {
        return this.dataStorage.count();
    }


}
