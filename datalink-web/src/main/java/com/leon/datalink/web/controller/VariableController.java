package com.leon.datalink.web.controller;

import com.leon.datalink.core.variable.Variable;
import com.leon.datalink.core.utils.ValidatorUtil;
import com.leon.datalink.web.service.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

/**
 * @ClassName VariableController
 * @Description 全局变量
 * @Author Leon
 * @Date 2022年8月16日18:40:50
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/variable")
public class VariableController {

    @Autowired
    private VariableService variableService;

    /**
     * 新增资源
     *
     * @param variable
     * @throws Exception
     */
    @PostMapping("/add")
    public void addVariable(@RequestBody Variable variable) throws Exception {
        ValidatorUtil.isNotEmpty(variable.getKey(), variable.getValue());
        Variable var = variableService.get(variable.getKey());
        if (null != var) throw new ValidationException("变量已存在");
        variableService.add(variable);
    }

    /**
     * 查询资源
     *
     * @param variable
     */
    @PostMapping("/list")
    public Object listVariable(@RequestBody Variable variable) {
        return variableService.list(variable);
    }

    /**
     * 移除资源
     *
     * @param variable
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeVariable(@RequestBody Variable variable) {
        String key = variable.getKey();
        ValidatorUtil.isNotEmpty(key);
        variableService.remove(key);
    }

    /**
     * 更新资源
     *
     * @param variable
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateVariable(@RequestBody Variable variable) {
        ValidatorUtil.isNotEmpty(variable.getKey(), variable.getValue());
        variableService.update(variable);
    }


}

