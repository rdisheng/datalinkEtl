package com.leon.datalink.web.config;

import akka.actor.ActorSystem;
import com.leon.datalink.cluster.ActorSystemFactory;
import com.leon.datalink.cluster.distributed.ConsistencyManager;
import com.leon.datalink.node.schedule.ScheduleManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * @ClassName AkkaConfig
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:07
 * @Version V1.0
 **/
@Configuration
public class AkkaConfig {

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem = ActorSystemFactory.create();
        ConsistencyManager.init(actorSystem);
        ScheduleManager.init(actorSystem);
        return actorSystem;
    }

    @PreDestroy
    public void destroy() {
        ConsistencyManager.onSystemDestroy();
        ScheduleManager.onSystemDestroy();
    }

}
