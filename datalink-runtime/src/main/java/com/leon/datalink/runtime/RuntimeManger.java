package com.leon.datalink.runtime;

import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.entity.RuntimeNode;
import com.leon.datalink.runtime.entity.RuntimeRule;
import com.leon.datalink.runtime.message.RuntimeData;
import com.leon.datalink.runtime.message.RuntimeStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManger {

    /**
     * 运行时列表
     */
    private static final ConcurrentHashMap<String, RuntimeRule> ruleRuntimeMap = new ConcurrentHashMap<>();

    //新建
    private static void createRuntime(String ruleId, List<String> nodeIdList) {
        RuntimeRule runtimeRule = new RuntimeRule();
        for (String nodeId : nodeIdList) {
            runtimeRule.put(nodeId, new RuntimeNode());
        }
        ruleRuntimeMap.put(ruleId, runtimeRule);
    }

    // 更新
    private static void updateRuntime(String ruleId, List<String> nodeIdList) {
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        RuntimeRule newRuntimeRule = new RuntimeRule();
        for (String nodeId : nodeIdList) {
            newRuntimeRule.put(nodeId, runtimeRule.getOrDefault(nodeId, new RuntimeNode()));
        }
        ruleRuntimeMap.put(ruleId, newRuntimeRule);
    }

    /**
     * 初始化 每次启动时调用
     */
    public static void initRuntime(String ruleId, List<String> nodeIdList) {
        if (ruleRuntimeMap.containsKey(ruleId)) {
            updateRuntime(ruleId, nodeIdList);
        } else {
            createRuntime(ruleId, nodeIdList);
        }
    }


    /**
     * 移除运行时
     *
     * @param ruleId
     */
    public static void removeRuntime(String ruleId) {
        ruleRuntimeMap.remove(ruleId);
    }


    /**
     * 处理数据记录
     *
     * @param ruleId
     * @param runtimeData
     */
    public static void handleData(String ruleId, RuntimeData runtimeData) {
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        if (null == runtimeRule) return;

        String nodeId = runtimeData.getNodeId();
        RuntimeNode runtimeNode = runtimeRule.get(nodeId);
        if (null == runtimeNode) return;

        runtimeNode.addMessage(runtimeData);
    }

    /**
     * 处理状态
     *
     * @param ruleId
     * @param runtimeStatus
     */
    public static void handleStatus(String ruleId, RuntimeStatus runtimeStatus) {
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        if (null == runtimeRule) return;

        String nodeId = runtimeStatus.getNodeId();
        RuntimeNode runtimeNode = runtimeRule.get(nodeId);
        if (null == runtimeNode) return;

        runtimeNode.setStatus(runtimeStatus.getStatus());
    }


    public static Map<String, RuntimeRule> getRuntimeList() {
        return ruleRuntimeMap;
    }


    public static void setRuntime(String ruleId, RuntimeRule runtime) {
        runtime.values().forEach(RuntimeNode::resetStatus);
        ruleRuntimeMap.put(ruleId, runtime);
    }


    public static Map<String, RuntimeStatusEnum> getRuleRuntimeStatus(String ruleId){
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        if (null == runtimeRule) return null;
        Map<String, RuntimeStatusEnum> result = new HashMap<>();
        runtimeRule.forEach((s, runtimeNode) -> result.put(s,runtimeNode.getStatus()));
        return result;
    }


    public static void resetRuleRuntimeStatus(String ruleId) {
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        if (null == runtimeRule) return;
        runtimeRule.values().forEach(RuntimeNode::resetStatus);
    }


    public static RuntimeNode getNodeRuntime(String ruleId, String nodeId) {
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        if (null == runtimeRule) return null;
        return runtimeRule.get(nodeId);
    }

    public static void resetNodeRuntime(String ruleId, String nodeId) {
        RuntimeRule runtimeRule = ruleRuntimeMap.get(ruleId);
        if (null == runtimeRule) return;
        RuntimeNode runtimeNode = runtimeRule.get(nodeId);
        if (null == runtimeNode) return;
        runtimeNode.resetCountAndData();
    }


}
