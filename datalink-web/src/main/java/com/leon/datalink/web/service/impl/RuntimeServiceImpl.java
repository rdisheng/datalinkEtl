package com.leon.datalink.web.service.impl;

import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.entity.RuntimeNode;
import com.leon.datalink.runtime.entity.RuntimeRule;
import com.leon.datalink.web.service.RuntimeService;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

@Service
public class RuntimeServiceImpl implements RuntimeService {

    private final DataStorage<String> dataStorage;

    public RuntimeServiceImpl() throws Exception {
        this.dataStorage = new ObjectStorage<>(String.class);
        if (this.dataStorage.count() <= 0) {
            return;
        }
        for (String ruleId : this.dataStorage.getKeys()) {
            String runtimeRuleJson = this.dataStorage.get(ruleId);
            RuntimeRule runtimeRule = JacksonUtils.toObj(runtimeRuleJson, RuntimeRule.class);
            RuntimeManger.setRuntime(ruleId, runtimeRule);
        }
    }

    @PreDestroy
    public void destroy() {
        // 系统停止时写入持久化
        Map<String, RuntimeRule> runtimeList = RuntimeManger.getRuntimeList();
        for (String key : runtimeList.keySet()) {
            RuntimeRule runtimeRule = runtimeList.get(key);
            runtimeRule.values().forEach(RuntimeNode::resetStatus);
            this.dataStorage.put(key, JacksonUtils.toJson(runtimeRule));
        }
    }


    @Override
    public void initRuntime(String ruleId, List<String> nodeIds) {
        RuntimeManger.initRuntime(ruleId, nodeIds);
    }

    @Override
    public void removeRuntime(String ruleId) {
        RuntimeManger.removeRuntime(ruleId);
        this.dataStorage.delete(ruleId);
    }

    @Override
    public Map<String, RuntimeStatusEnum> getRuleRuntimeStatus(String ruleId) {
        return RuntimeManger.getRuleRuntimeStatus(ruleId);
    }

    @Override
    public void resetRuleRuntimeStatus(String ruleId) {
        RuntimeManger.resetRuleRuntimeStatus(ruleId);
    }

    @Override
    public RuntimeNode getNodeRuntime(String ruleId, String nodeId) {
        return RuntimeManger.getNodeRuntime(ruleId, nodeId);
    }

    @Override
    public void resetNodeRuntime(String ruleId, String nodeId) {
        RuntimeManger.resetNodeRuntime(ruleId, nodeId);
    }
}
