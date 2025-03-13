package com.leon.datalink.web.controller;

import com.leon.datalink.core.monitor.Listener;
import com.leon.datalink.web.service.ListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ListenerController
 * @Description 监听器
 * @Author Leon
 * @Date 2022年9月3日13:40:00
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/listener")
public class ListenerController {

    @Autowired
    private ListenerService listenerService;

    /**
     * 查询
     */
    @PostMapping("/list")
    public Object list(@RequestBody Listener listener) {
        return listenerService.list(listener);
    }

}

