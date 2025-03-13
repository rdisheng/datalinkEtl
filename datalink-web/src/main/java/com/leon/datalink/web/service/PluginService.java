package com.leon.datalink.web.service;


import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.node.plugin.Plugin;

public interface PluginService extends BaseService<Plugin> {

    void upload(String fileName,byte[] file) ;

    byte[] download(String fileName) ;

}
