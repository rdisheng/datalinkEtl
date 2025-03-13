package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateUtil;

import com.leon.datalink.node.plugin.Plugin;
import com.leon.datalink.web.service.PluginService;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.core.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @ClassName RulesController
 * @Description 插件管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/plugin")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    @Autowired
    private RuleService ruleService;

    /**
     * 上传jar文件
     *
     * @param jarFile
     * @throws IOException
     */
    @PostMapping(value = "/upload", consumes = "multipart/*", headers = "Content-Type=multipart/form-data")
    public Object upload(@RequestParam("file") MultipartFile jarFile) throws Exception {
        ValidatorUtil.isNotNull(jarFile, jarFile.getOriginalFilename());
        String fileName = jarFile.getOriginalFilename();

//        List<Plugin> list = pluginService.list(new Plugin().setFileName(fileName));
//        if (CollectionUtil.isNotEmpty(list)) {
//            throw new ValidationException("插件已存在");
//        }

        pluginService.upload(fileName, jarFile.getBytes());

        HashMap<String, String> result = new HashMap<>();
        result.put("fileName", fileName);
        return result;
    }


    /**
     * 下载插件文件
     */
    @GetMapping(value = "/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/octet-stream");
        OutputStream out = response.getOutputStream();
        out.write(pluginService.download(fileName));
        out.flush();
    }

    /**
     * 查询插件
     *
     * @param pluginId
     * 
     */
    @GetMapping("/info")
    public Object getPlugin(@RequestParam(value = "pluginId") String pluginId) {
        return pluginService.get(pluginId);
    }


    /**
     * 新增插件
     *
     * @param plugin
     * 
     */
    @PostMapping("/add")
    public void addPlugin(@RequestBody Plugin plugin) throws Exception {
        ValidatorUtil.isNotEmpty(plugin.getPluginName(), plugin.getPackagePath());
        plugin.setUpdateTime(DateUtil.now());
        pluginService.add(plugin);
    }

    /**
     * 查询插件
     *
     * @param plugin
     */
    @PostMapping("/list")
    public Object listPlugin(@RequestBody Plugin plugin) {
        return pluginService.list(plugin);
    }

    /**
     * 移除插件
     *
     * @param plugin
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removePlugin(@RequestBody Plugin plugin) throws Exception {
        String pluginId = plugin.getPluginId();
        ValidatorUtil.isNotEmpty(pluginId);

//        Rule rule = new Rule();
//        rule.setSearchPluginId(pluginId);
//        List<Rule> list = ruleService.list(rule);
//        if (CollectionUtil.isNotEmpty(list)) {
//            throw new ValidationException("该插件已被规则绑定,无法删除");
//        }

        pluginService.remove(pluginId);
    }

    /**
     * 更新插件
     *
     * @param plugin
     * @throws Exception
     */
    @PutMapping("/update")
    public void updatePlugin(@RequestBody Plugin plugin) throws Exception {
        ValidatorUtil.isNotEmpty(plugin.getPluginId(), plugin.getPluginName(), plugin.getPackagePath());
        plugin.setUpdateTime(DateUtil.now());
        pluginService.update(plugin);
    }

}

