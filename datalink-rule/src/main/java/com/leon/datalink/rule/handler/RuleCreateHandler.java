package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import com.leon.datalink.rule.entity.Rule;

import java.util.List;
import java.util.Map;

public interface RuleCreateHandler {

    Map<String, Map<Integer, List<ActorRef>>> create(Rule rule, ActorContext actorContext);

}
