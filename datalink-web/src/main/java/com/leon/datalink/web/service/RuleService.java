package com.leon.datalink.web.service;

import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.rule.entity.Rule;

public interface RuleService extends BaseService<Rule> {

    void startRule(String ruleId) throws Exception;

    void stopRule(String ruleId) throws Exception;

}
