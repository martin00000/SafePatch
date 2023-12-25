package com.aland.plugin;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class PluginManager
 * <p>
 * Created by aland on 2023/12/25.
 *
 * @version 1.0
 */
public class PluginManager {

    private PluginLoader pluginLoader;
    private List<Plugin> plugins = new ArrayList<>();



    public PluginManager(Instrumentation instrumentation, Properties properties, URL scanPluginLocation) {

        pluginLoader = new PluginLoader(instrumentation, properties, scanPluginLocation);

    }

    public static ClassLoader getAgentLibClassLoader() {
        return PluginLoader.agentLibClassLoader;
    }

    public static void setAgentLibClassLoader(ClassLoader agentLibClassLoader) {
        PluginLoader.agentLibClassLoader = agentLibClassLoader;
    }


    synchronized public void enablePlugins(List<String> blackPlugins) {
        for (Plugin plugin : plugins) {
            try {
                if (blackPlugins.contains(plugin.name())) {
                    plugin.setState(PluginState.DISABLED);
//                    logger.info(" {}  enable:  false because of diamond black plugins.", plugin.name());
                } else {
                    boolean result = plugin.enabled();
//                    logger.info(" {}  enable:  {}", plugin.name(), result);
                }
            } catch (PluginException e) {
//                logger.error("enabled plugin {} error.", plugin.name(), e);
            }
        }
    }

    synchronized public void initPlugins() throws PluginException {
//        logger.info("Init available plugins");
        for (Plugin plugin : plugins) {
            if (plugin.state() == PluginState.ENABLED) {
                updateState(plugin, PluginState.INITING);
//                logger.debug("Init plugin : {}", plugin.name());
                plugin.init();
//                logger.debug("Init plugin : {} succeeded", plugin.name());
                updateState(plugin, PluginState.INITED);
            } else {
//                logger.info("skip init plugin, name: {}, state: {}", plugin.name(), plugin.state());
            }
        }
    }

    synchronized public void startPlugins() throws PluginException {
//        logger.info("Starting available plugins");
        for (Plugin plugin : plugins) {
            if (plugin.state() == PluginState.INITED) {
                updateState(plugin, PluginState.STARTING);
//                logger.debug("Start plugin : {}", plugin.name());
                plugin.start();
//                logger.debug("Start plugin : {} succeeded", plugin.name());
                updateState(plugin, PluginState.STARTED);
            } else {
//                logger.debug("skip start plugin, name: {}, state: {}", plugin.name(), plugin.state());
            }
        }
    }

    synchronized public void stopPlugins() throws PluginException {
//        logger.info("Stopping available plugins");
        for (Plugin plugin : plugins) {
            if (plugin.state() == PluginState.STARTED) {
                updateState(plugin, PluginState.STOPPING);
//                logger.debug("Stop plugin : {}", plugin.name());
                plugin.stop();
//                logger.debug("Stop plugin : {} succeeded", plugin.name());
                updateState(plugin, PluginState.STOPED);
            } else {
//                logger.debug("skip stop plugin, name: {}, state: {}", plugin.name(), plugin.state());
            }

        }
    }

    private void updateState(Plugin plugin, PluginState state) {
         plugin.setState(state);
    }




}
