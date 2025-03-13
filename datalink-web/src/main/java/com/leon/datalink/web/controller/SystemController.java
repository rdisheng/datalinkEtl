package com.leon.datalink.web.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.leon.datalink.cluster.member.ClusterMemberManager;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.utils.VersionUtils;
import com.leon.datalink.resource.entity.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.model.DiskInfo;
import com.leon.datalink.web.model.SystemInfo;
import com.leon.datalink.web.model.SystemStatistics;
import com.leon.datalink.web.service.ResourceService;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.core.utils.BaseContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName SystemInfoController
 * @Description
 * @Author Leon
 * @Date 2022/4/11 10:34
 * @Version V1.0
 **/
@RestController
@RequestMapping({"/api/system"})
public class SystemController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RuleService ruleService;

    @GetMapping("/info")
    public Object getSystemInfo() {
        SystemInfo info = new SystemInfo();
        info.setLocalMemberName(ClusterMemberManager.getLocalMemberName());
        info.setVersion(VersionUtils.version);
        info.setTime(DateUtil.format(DateTime.now(), "yyyy-MM-dd HH:mm"));
        info.setUsername((String) BaseContextUtil.get(Constants.USERNAME));
        return info;
    }

    @GetMapping("/statistics")
    public Object getSystemStatistics() {
        SystemStatistics statistics = new SystemStatistics();
        statistics.setResourceCount(resourceService.getCount());
        statistics.setRuleCount(ruleService.getCount());
        return statistics;
    }

    @GetMapping("/disk")
    public Object getDiskInfo() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] fs = File.listRoots();
        return Arrays.stream(fs).map(disk -> {
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setName(fsv.getSystemDisplayName(disk));
            diskInfo.setTotal(disk.getTotalSpace());
            diskInfo.setFree(disk.getFreeSpace());
            return diskInfo;
        }).collect(Collectors.toList());
    }


}
