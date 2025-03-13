package com.leon.datalink.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.model.PointsExportVO;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.core.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RulesController
 * @Description 规则管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;


    /**
     * 查询规则
     *
     * @param ruleId
     */
    @GetMapping("/info")
    public Object getRule(@RequestParam(value = "ruleId") String ruleId) {
        return ruleService.get(ruleId);
    }


    /**
     * 新增规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/add")
    public Object addRule(@RequestBody Rule rule) throws Exception {
        ValidatorUtil.isNotEmpty(rule.getRuleName(), rule.getGraphJson());
        ruleService.add(rule);
        return new Rule().setRuleId(rule.getRuleId());
    }

    /**
     * 查询规则
     *
     * @param rule
     */
    @PostMapping("/list")
    public Object listRule(@RequestBody Rule rule) {
        return ruleService.list(rule);
    }

    /**
     * 移除规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeRule(@RequestBody Rule rule) {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.remove(ruleId);
    }

    /**
     * 更新规则
     *
     * @param rule
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateRule(@RequestBody Rule rule) {
        ValidatorUtil.isNotEmpty(rule.getRuleId(), rule.getRuleName(), rule.getGraphJson());
        ruleService.update(rule);
    }

    /**
     * 启动规则
     */
    @PostMapping("/startAndSave")
    public void startAndSave(@RequestBody Rule rule) throws Exception {
        ValidatorUtil.isNotEmpty(rule.getRuleId(), rule.getRuleName(), rule.getGraphJson());
        ruleService.update(rule);
        ruleService.startRule(rule.getRuleId());
    }

    /**
     * 启动规则
     */
    @PostMapping("/start")
    public void startRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.startRule(ruleId);
    }

    /**
     * 停止规则
     */
    @PostMapping("/stop")
    public void stopRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.stopRule(ruleId);
    }

    /**
     * 重启规则
     */
    @PostMapping("/restart")
    public void restartRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.update(rule);
        ruleService.stopRule(ruleId);
        ruleService.startRule(ruleId);
    }

    /**
     * 导入点位
     *
     * @param excelFile
     * @throws IOException
     */
    @PostMapping(value = "/points/import", consumes = "multipart/*", headers = "Content-Type=multipart/form-data")
    public List<Map<String, Object>> importPoints(@RequestParam("file") MultipartFile excelFile, @RequestParam("field") String fieldMap) throws Exception {
        ValidatorUtil.isNotNull(excelFile, excelFile.getOriginalFilename());
        ExcelReader reader = ExcelUtil.getReader(excelFile.getInputStream());
        reader.setHeaderAlias(JacksonUtils.toObj(fieldMap.getBytes(), new TypeReference<Map<String, String>>() {
        }));
        return reader.readAll();
    }

    /**
     * 导出点位
     */
    @PostMapping(value = "/points/export")
    public void exportPoints(@RequestBody PointsExportVO body, HttpServletResponse response) throws IOException {
        Map<String, String> fieldMap = body.getFieldMap();
        List<Object> data = body.getData();
        if (CollectionUtil.isEmpty(data)) {
            Map<String, Object> emptyData = new HashMap<>();
            fieldMap.keySet().forEach(key -> emptyData.put(key, null));
            data = Lists.newArrayList(emptyData);
        }
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.setHeaderAlias(fieldMap);
        writer.write(data);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + body.getFileName());
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }


}

