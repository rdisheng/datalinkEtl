package com.leon.datalink.web.controller;

import com.leon.datalink.web.service.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RuntimeController
 * @Description 运行状态管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/runtime")
public class RuntimeController {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 查询节点运行状态
     *
     * @param ruleId
     */
    @GetMapping("/node/info")
    public Object getNodeRuntime(@RequestParam(value = "ruleId") String ruleId, @RequestParam(value = "nodeId") String nodeId) {
        return runtimeService.getNodeRuntime(ruleId, nodeId);
    }


    /**
     * 重置节点运行状态
     *
     * @param ruleId
     */
    @GetMapping("/node/reset")
    public void resetNodeRuntime(@RequestParam(value = "ruleId") String ruleId, @RequestParam(value = "nodeId") String nodeId) {
        runtimeService.resetNodeRuntime(ruleId, nodeId);
    }

    /**
     * 查询规则运行状态
     *
     * @param ruleId
     */
    @GetMapping("/rule/info")
    public Object getRuleRuntimeStatus(@RequestParam(value = "ruleId") String ruleId) {
        return runtimeService.getRuleRuntimeStatus(ruleId);
    }


}

