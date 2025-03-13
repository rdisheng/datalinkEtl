package com.leon.datalink.plugin;


import java.util.Map;

public interface DataLinkPlugin {

    /**
     * 启动
     *
     * @param configJson    配置
     * @param context       上下文
     * @param outputChannel 输出通道
     * @throws Exception
     */
    void onStart(String configJson, Map<String, Object> context, OutputChannel outputChannel) throws Exception;


    /**
     * 停止
     *
     * @throws Exception
     */
    void onStop() throws Exception;

    /**
     * 输入
     *
     * @param context       上下文
     * @param variable      变量
     * @param data          上游数据
     * @param outputChannel
     * @throws Exception
     */
    void onInput(Map<String, Object> context, Map<String, Object> variable, Object data, OutputChannel outputChannel) throws Exception;

}
