package com.leon.datalink.node.plugin;

import com.leon.datalink.core.utils.ClassUtil;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.plugin.DataLinkPlugin;
import org.reflections.Reflections;

import java.util.Set;


/**
 * 插件
 */
public class PluginFactory {

    public static DataLinkPlugin createPlugin(String jarPath, String packagePath) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (StringUtils.isEmpty(jarPath) || StringUtils.isEmpty(packagePath)) return null;
        ClassUtil.loadJar(jarPath);
        Reflections reflections = new Reflections(packagePath);
        Set<Class<? extends DataLinkPlugin>> set = reflections.getSubTypesOf(DataLinkPlugin.class);
        if (!set.isEmpty()) {
            return set.stream().findFirst().get().newInstance();
        }
        return null;
    }

}
