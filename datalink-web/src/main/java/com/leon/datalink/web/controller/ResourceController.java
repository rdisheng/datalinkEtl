package com.leon.datalink.web.controller;

import com.leon.datalink.resource.entity.Resource;
import com.leon.datalink.web.service.ResourceService;
import com.leon.datalink.core.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ResourceController
 * @Description 资源管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 查询资源
     *
     * @param resourceId
     */
    @GetMapping("/info")
    public Object getResource(@RequestParam(value = "resourceId") String resourceId) {
        return resourceService.get(resourceId);
    }


    /**
     * 新增资源
     *
     * @param resource
     * @throws Exception
     */
    @PostMapping("/add")
    public Object addResource(@RequestBody Resource resource) throws Exception {
        ValidatorUtil.isNotEmpty(resource.getResourceName(), resource.getResourceType(), resource.getProperties());
        resourceService.add(resource);
        return new Resource().setResourceId(resource.getResourceId());
    }

    /**
     * 查询资源
     *
     * @param resource
     */
    @PostMapping("/list")
    public Object listResource(@RequestBody Resource resource) {
        return resourceService.list(resource);
    }

    /**
     * 移除资源
     *
     * @param resource
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeResource(@RequestBody Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        ValidatorUtil.isNotEmpty(resourceId);
        resourceService.remove(resourceId);
    }

    /**
     * 更新资源
     *
     * @param resource
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateResource(@RequestBody Resource resource) throws Exception {
        ValidatorUtil.isNotEmpty(resource.getResourceId(), resource.getResourceName(), resource.getResourceType(), resource.getProperties());
        resourceService.update(resource);
    }

    /**
     * 测试资源
     *
     * @param resource
     * @throws Exception
     */
    @PostMapping("/test")
    public boolean testResource(@RequestBody Resource resource) throws Exception {
        ValidatorUtil.isNotEmpty(resource.getResourceType(), resource.getProperties());
        return resourceService.testDriver(resource);
    }

}

