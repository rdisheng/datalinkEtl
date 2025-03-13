package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.ScriptUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.script.Script;
import com.leon.datalink.web.model.ScriptParamVO;
import com.leon.datalink.web.service.ScriptService;
import com.leon.datalink.core.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RulesController
 * @Description 脚本管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/script")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    /**
     * 新增脚本
     *
     * @param scriptId
     */
    @GetMapping("/info")
    public Object getScript(@RequestParam(value = "scriptId") String scriptId) {
        return scriptService.get(scriptId);
    }


    /**
     * 新增脚本
     *
     * @param script
     */
    @PostMapping("/add")
    public Object addScript(@RequestBody Script script) {
        ValidatorUtil.isNotEmpty(script.getScriptName(), script.getScriptContent());
        script.setUpdateTime(DateUtil.now());
        scriptService.add(script);
        return new Script().setScriptId(script.getScriptId());
    }

    /**
     * 查询脚本
     *
     * @param script
     */
    @PostMapping("/list")
    public Object listScript(@RequestBody Script script) {
        return scriptService.list(script);
    }

    /**
     * 移除脚本
     *
     * @param script
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeScript(@RequestBody Script script) {
        ValidatorUtil.isNotEmpty(script.getScriptId());
        scriptService.remove(script.getScriptId());
    }

    /**
     * 更新脚本
     *
     * @param script
     * @throws Exception
     */
    @PutMapping("/update")
    public Object updateScript(@RequestBody Script script) {
        ValidatorUtil.isNotEmpty(script.getScriptId(), script.getScriptName(), script.getScriptContent());
        script.setUpdateTime(DateUtil.now());
        scriptService.update(script);
        return script;
    }

    /**
     * 运行调试脚本
     */
    @PostMapping("/test")
    public Object testScript(@RequestBody ScriptParamVO scriptParamVO) throws Exception {
        String language = scriptParamVO.getLanguage();
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(language);
        long time1 = System.currentTimeMillis();
        // 获取并绑定自定义环境变量
        Bindings bind = scriptEngine.createBindings();

        Map<String, Object> globalVariable = GlobalVariableContent.getAllValue();
        for (String key : globalVariable.keySet()) {
            bind.put(key, globalVariable.get(key));
        }

        scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(scriptParamVO.getScript());
        Invocable jsInvoke = (Invocable) scriptEngine;
        Object transform = jsInvoke.invokeFunction("transform", JacksonUtils.toObj(scriptParamVO.getParam(), Object.class));

        long time2 = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        result.put("result", "javascript".equals(language) ? ScriptUtil.toJavaObject(transform) : transform);
        result.put("time", time2 - time1);
        return result;
    }

}

