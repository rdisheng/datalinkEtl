package com.leon.datalink.web.service.impl;

import com.google.common.collect.Lists;
import com.leon.datalink.cluster.distributed.ConsistencyManager;
import com.leon.datalink.cluster.distributed.ConsistencyWrapper;
import com.leon.datalink.core.backup.BackupData;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.core.variable.Variable;
import com.leon.datalink.core.variable.VariableTypeEnum;
import com.leon.datalink.web.service.VariableService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VariableServiceImpl implements VariableService, BackupData<Variable> {

    private final DataStorage<Variable> dataStorage;


    public VariableServiceImpl() throws Exception {
        this.dataStorage = new ObjectStorage<>(Variable.class);
        GlobalVariableContent.setAllCustomVariables(new HashSet<>(this.dataStorage.getValues()));
    }

    @Override
    public Variable get(String id) {
        return GlobalVariableContent.get(id);
    }

    @Override
    public void add(Variable variable) {
        variable.setType(VariableTypeEnum.CUSTOM);
        GlobalVariableContent.set(variable.getKey(), variable);
        this.dataStorage.put(variable.getKey(), variable);
        ConsistencyManager.sync(ConsistencyWrapper.add(Variable.class, variable));
    }

    @Override
    public void update(Variable variable) {
        if (!VariableTypeEnum.CUSTOM.equals(variable.getType())) return;
        GlobalVariableContent.set(variable.getKey(), variable);
        this.dataStorage.put(variable.getKey(), variable);
        ConsistencyManager.sync(ConsistencyWrapper.add(Variable.class, variable));
    }

    @Override
    public void remove(String key) {
        Variable variable = this.get(key);
        if (!VariableTypeEnum.CUSTOM.equals(variable.getType())) return;
        GlobalVariableContent.remove(key);
        this.dataStorage.delete(key);
        ConsistencyManager.sync(ConsistencyWrapper.delete(Variable.class, variable));
    }

    @Override
    public List<Variable> list(Variable variable) {
        Map<String, Variable> all = GlobalVariableContent.getAll();
        Stream<Variable> stream = Lists.newArrayList(all.values()).stream();
        if (null != variable) {
            if (!StringUtils.isEmpty(variable.getKey())) {
                stream = stream.filter(r -> r.getKey().contains(variable.getKey()));
            }
            if (null != variable.getType()) {
                stream = stream.filter(r -> r.getType().equals(variable.getType()));
            }
        }
        return stream.sorted(Comparator.comparing(Variable::getType).thenComparing(Variable::getKey)).collect(Collectors.toList());
    }

    @Override
    public long getCount() {
        return GlobalVariableContent.getAll().size();
    }

    @Override
    public String dataKey() {
        return "variables";
    }

    @Override
    public List<Variable> createBackup() {
        Variable variable = new Variable();
        variable.setType(VariableTypeEnum.CUSTOM);
        return this.list(variable);
    }

    @Override
    public void recoverBackup(List<Variable> dataList) {
        Variable variable = new Variable();
        variable.setType(VariableTypeEnum.CUSTOM);
        List<Variable> list = this.list(variable);
        for (Variable var : list) {
            this.remove(var.getKey());
        }
        for (Variable var : dataList) {
            this.add(var);
        }
    }
}
