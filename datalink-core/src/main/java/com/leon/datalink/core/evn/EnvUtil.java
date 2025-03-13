package com.leon.datalink.core.evn;

import com.leon.datalink.core.utils.StringUtils;
import org.springframework.core.env.ConfigurableEnvironment;

import java.nio.file.Paths;

public class EnvUtil {

    private static final String DATALINK_HOME = "datalink.home";

    private static final String USER_DIR = "user.dir";

    private static final String CLUSTER = "datalink.cluster";

    private static final String STORAGE_PATH = "data";

    private static final String PLUGIN_FILE_PATH = "plugin";

    public static final String BACKUP_FILE_PATH = "backup";

    private static final String LOCAL_IP = "datalink.local.ip";

    private static final String CLUSTER_MEMBER_LIST_PROPERTY = "datalink.cluster.member-list"; // 启动脚本

    private static final String CLUSTER_MEMBER_LIST_ENV = "datalink.cluster.member.list"; // 配置文件

    private static final String CLUSTER_INSTANCES_MAX = "datalink.cluster.instances.max";

    private static final String RUNTIME_RECORD_LIMIT = "datalink.rule.runtime.record.limit";

    private static final String CA_CRT_FILE = "cert/datalink.crt";

    private static String clusterMemberList;

    private static Integer clusterInstancesMax;

    private static Integer runtimeRecordLimit;

    private static String localIp;

    private static String datalinkHome;

    private static String userDir;

    private static String baseDir;

    private static String strongPath;

    private static String pluginFilePath;

    private static String backFilePath;

    private static Boolean isCluster;

    private static ConfigurableEnvironment environment;

    public static void setEnvironment(ConfigurableEnvironment environment) {
        EnvUtil.environment = environment;
    }

    public static boolean isCluster() {
        if (null == isCluster) {
            isCluster = Boolean.parseBoolean(System.getProperty(CLUSTER));
        }
        return isCluster;
    }

    public static String getClusterMemberList() {
        if (StringUtils.isBlank(clusterMemberList)) {
            clusterMemberList = System.getProperty(CLUSTER_MEMBER_LIST_PROPERTY);
            if (StringUtils.isBlank(clusterMemberList)) {
                clusterMemberList = environment.getProperty(CLUSTER_MEMBER_LIST_ENV);
            }
        }
        return clusterMemberList;
    }

    public static Integer getClusterInstancesMax() {
        if (null == clusterInstancesMax) {
            clusterInstancesMax = environment.getProperty(CLUSTER_INSTANCES_MAX, Integer.class);
        }
        return clusterInstancesMax;
    }

    public static String getLocalIp() {
        if (StringUtils.isBlank(localIp)) {
            localIp = System.getProperty(LOCAL_IP);
        }
        return localIp;
    }

    public static void setLocalIp(String localIp) {
        EnvUtil.localIp = localIp;
        System.setProperty(LOCAL_IP, localIp);
    }

    public static String getDatalinkHome() {
        if (StringUtils.isBlank(datalinkHome)) {
            datalinkHome = System.getProperty(DATALINK_HOME);
        }
        return datalinkHome;
    }

    public static String getUserDir() {
        if (StringUtils.isBlank(userDir)) {
            userDir = System.getProperty(USER_DIR);
        }
        return userDir;
    }

    public static String getBaseDir() {
        if (StringUtils.isBlank(baseDir)) {
            String datalinkHome = getDatalinkHome();
            baseDir = StringUtils.isEmpty(datalinkHome) ? getUserDir() : datalinkHome;
        }
        return baseDir;
    }

    public static String getStoragePath() {
        if (StringUtils.isBlank(strongPath)) {
            strongPath = Paths.get(getBaseDir(), STORAGE_PATH).toString();
        }
        return strongPath;
    }

    public static String getPluginFilePath() {
        if (StringUtils.isBlank(pluginFilePath)) {
            pluginFilePath = Paths.get(getBaseDir(), PLUGIN_FILE_PATH).toString();
        }
        return pluginFilePath;
    }

    public static String getBackupFilePath() {
        if (StringUtils.isBlank(backFilePath)) {
            backFilePath = Paths.get(getBaseDir(), BACKUP_FILE_PATH).toString();
        }
        return backFilePath;
    }

    public static Integer getRuntimeRecordLimit() {
        if (null == runtimeRecordLimit) {
            runtimeRecordLimit = environment.getProperty(RUNTIME_RECORD_LIMIT, Integer.class);
        }
        return runtimeRecordLimit;
    }

    public static String getCaCrtFile() {
        return CA_CRT_FILE;
    }

}
