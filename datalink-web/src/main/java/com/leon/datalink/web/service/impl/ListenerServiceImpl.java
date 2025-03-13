package com.leon.datalink.web.service.impl;

import com.leon.datalink.core.monitor.Listener;
import com.leon.datalink.core.monitor.ListenerContent;
import com.leon.datalink.core.monitor.ListenerTypeEnum;
import com.leon.datalink.web.service.ListenerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ListenerServiceImpl implements ListenerService {

    @Value("${server.port}")
    private int port;

    @PostConstruct
    private void init() {
        ListenerContent.add(port, ListenerTypeEnum.TCP, "Datalink server port");
    }

    @Override
    public List<Listener> list(Listener listener) {
        Stream<Listener> stream = ListenerContent.getList().stream();
        if (null != listener) {
            if (null != listener.getPort()) {
                stream = stream.filter(r -> r.getPort().equals(listener.getPort()));
            }
        }
        return stream.collect(Collectors.toList());
    }

}
