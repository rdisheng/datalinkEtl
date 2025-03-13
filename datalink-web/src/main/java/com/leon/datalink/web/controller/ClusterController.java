package com.leon.datalink.web.controller;

import com.leon.datalink.cluster.member.ClusterMember;
import com.leon.datalink.web.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ClusterController
 * @Description 集群
 * @Author Leon
 * @Date 2023年1月7日10:47:31
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    @Autowired
    private ClusterService clusterService;

    /**
     * 查询
     */
    @PostMapping("/list")
    public Object list(@RequestBody ClusterMember clusterMember) {
        return clusterService.list(clusterMember);
    }

}

