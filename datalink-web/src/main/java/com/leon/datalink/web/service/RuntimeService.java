package com.leon.datalink.web.service;

import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.entity.RuntimeNode;
import java.util.List;
import java.util.Map;


public interface RuntimeService {

    void initRuntime(String ruleId, List<String> nodeIds);

    void removeRuntime(String ruleId);

    Map<String, RuntimeStatusEnum> getRuleRuntimeStatus(String ruleId);

    void resetRuleRuntimeStatus(String ruleId);

    RuntimeNode getNodeRuntime(String ruleId,String nodeId);

    void resetNodeRuntime(String ruleId, String nodeId);

}
