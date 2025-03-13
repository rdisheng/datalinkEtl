package com.leon.datalink.node.schedule;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.message.NodeDataMessage;
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension;
import scala.Option;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public class ScheduleManager {

    private static QuartzSchedulerExtension quartzSchedule;

    private static ActorSystem actorSystem;

    public static void init(ActorSystem as) {
        actorSystem = as;
    }

    public static void create(ActorRef actorRef, ScheduleParam scheduleParam) {
        if (quartzSchedule == null) {
            quartzSchedule = new QuartzSchedulerExtension(actorSystem);
        }
        quartzSchedule.createSchedule(scheduleParam.getNodeId(), Option.empty(), scheduleParam.getCronExpression(), Option.empty(), TimeZone.getDefault());

        NodeDataMessage nodeDataMessage = new NodeDataMessage("schedule trigger " +System.currentTimeMillis());
        Long initialDelay = scheduleParam.getInitialDelay();
        String initialDelayUnit = scheduleParam.getInitialDelayUnit();
        if (initialDelay == null || initialDelay == 0 || StringUtils.isEmpty(initialDelayUnit)) {
            // 无延迟启动
            quartzSchedule.schedule(scheduleParam.getNodeId(), actorRef, nodeDataMessage);
        } else {
            // 有延迟启动
            ZonedDateTime jobStartTime = Instant.now().atZone(ZoneId.systemDefault()).plus(Duration.of(initialDelay, ChronoUnit.valueOf(initialDelayUnit)));
            quartzSchedule.schedule(scheduleParam.getNodeId(), actorRef, nodeDataMessage, Option.apply(Date.from(jobStartTime.toInstant())));
        }
    }

    public static void stop(String nodeId) {
        quartzSchedule.deleteJobSchedule(nodeId);
    }


    public static void onSystemDestroy() {
        if (quartzSchedule != null) {
            quartzSchedule.deleteAll();
            quartzSchedule.shutdown(false);
        }
    }

}
