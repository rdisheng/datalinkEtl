package com.leon.datalink.node.actor;

import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import com.leon.datalink.node.plugin.Plugin;
import com.leon.datalink.node.plugin.PluginFactory;
import com.leon.datalink.plugin.DataLinkPlugin;
import com.leon.datalink.plugin.OutputChannel;

import java.nio.file.Paths;
import java.util.Map;


public class PluginNodeActor extends AbstractNodeActor {

    private DataLinkPlugin dataLinkPlugin;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        // 创建插件
        Plugin plugin = properties.getObject("plugin", Plugin.class);
        this.dataLinkPlugin = PluginFactory.createPlugin(Paths.get(EnvUtil.getPluginFilePath(), plugin.getFileName()).toString(), plugin.getPackagePath());
        if (null != dataLinkPlugin) {
            dataLinkPlugin.onStart(properties.getString("configJson"), context, new OutputChannel() {
                @Override
                public void out(Object data) {
                    output.out(data);
                }

                @Override
                public void out(int portIndex, Object data) {
                    output.out(portIndex, data);
                }
            });
        } else {
            throw new RuntimeException("插件加载失败");
        }
    }

    @Override
    public void destroy() throws Exception {
        if (null != dataLinkPlugin) {
            dataLinkPlugin.onStop();
        }
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) throws Exception {
        Map<String, Object> allValue = GlobalVariableContent.getAllValue();
        if (null == dataLinkPlugin) {
            throw new RuntimeException("插件未加载");
        }
        dataLinkPlugin.onInput(context, allValue, data, new OutputChannel() {
            @Override
            public void out(Object data) {
                output.out(data);
            }

            @Override
            public void out(int portIndex, Object data) {
                output.out(portIndex, data);
            }
        });
    }


}
