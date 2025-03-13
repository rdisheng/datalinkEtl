package com.leon.datalink.node.behavior;

import com.leon.datalink.core.config.ConfigProperties;

@FunctionalInterface
public interface NodeClusterBehavior {
    NodeClusterBehaviorEnum get(ConfigProperties configProperties);
}
