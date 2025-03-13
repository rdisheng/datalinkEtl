package com.leon.datalink.web.service;

import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.resource.entity.Resource;

public interface ResourceService extends BaseService<Resource> {

    boolean testDriver(Resource resource) throws Exception;

}
