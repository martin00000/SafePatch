package com.aland.agent.plugin;

import java.io.Serializable;

/**
 * <p>
 * The PluginConfig class is a plugin configuration class.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/29
 **/
public class PluginConfig implements Serializable {
    private String version;
    private String name;
    private String pluginActivator;
    private int order = 1000;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPluginActivator() {
        return pluginActivator;
    }

    public void setPluginActivator(String pluginActivator) {
        this.pluginActivator = pluginActivator;
    }
}