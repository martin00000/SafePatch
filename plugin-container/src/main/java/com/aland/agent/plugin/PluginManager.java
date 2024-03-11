package com.aland.agent.plugin;

import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * This class is responsible for loading, enabling, initializing, starting, and stopping plugins.
 * It uses a PluginLoader to load plugins from a specified location and manages the state of each plugin.
 * The PluginManager class provides synchronized methods for safe concurrent access to plugin operations.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/25
 **/
public class PluginManager {
    private PluginLoader pluginLoader;
    // list of plugins
    private List<Plugin> plugins = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(PluginManager.class);

    /**
     * Constructs a PluginManager object with the specified instrumentation, properties, and plugin location.
     *
     * @param instrumentation   the instrumentation object for loading plugins
     * @param properties        the properties object for configuring plugins
     * @param scanPluginLocation the URL of the location to scan for plugins
     */
    public PluginManager(Instrumentation instrumentation, Properties properties, URL scanPluginLocation) {
        pluginLoader = new PluginLoader(instrumentation, properties, scanPluginLocation);
    }

    /**
     * Loads the plugins using the plugin loader.
     *
     * @throws PluginException if an error occurs during plugin loading
     */
    synchronized public void loadPlugins() throws PluginException {
        plugins = pluginLoader.load();
    }

    /**
     * Enables the plugins, excluding the ones specified in the blackPlugins list.
     *
     * @param blackPlugins the list of blacklisted plugin names
     */
    synchronized public void enablePlugins(List<String> blackPlugins) {
        for (Plugin plugin : plugins) {
            try {
                if (blackPlugins.contains(plugin.name())) {
                    plugin.setState(PluginState.DISABLED);
                    logger.info(" {}  enable:  false because of diamond black plugins.", plugin.name());
                } else {
                    boolean result = plugin.enabled();
                    logger.info(" {}  enable:  {}", plugin.name(), result);
                }
            } catch (PluginException e) {
                logger.error("enabled plugin {} error.", plugin.name(), e);
            }
        }
    }

    /**
     * Initializes the enabled plugins.
     *
     * @throws PluginException if an error occurs during plugin initialization
     */
    synchronized public void initPlugins() throws PluginException {
        logger.info("Init available plugins");
        for (Plugin plugin : plugins) {
            if (plugin.state() == PluginState.ENABLED) {
                updateState(plugin, PluginState.INITING);
                logger.debug("Init plugin : {}", plugin.name());
                plugin.init();
                logger.debug("Init plugin : {} succeeded", plugin.name());
                updateState(plugin, PluginState.INITED);
            } else {
                logger.info("skip init plugin, name: {}, state: {}", plugin.name(), plugin.state());
            }
        }
    }

    /**
     * Starts the initialized plugins.
     *
     * @throws PluginException if an error occurs during plugin starting
     */
    synchronized public void startPlugins() throws PluginException {
        logger.info("Starting available plugins");
        for (Plugin plugin : plugins) {
            if (plugin.state() == PluginState.INITED) {
                updateState(plugin, PluginState.STARTING);
                logger.debug("Start plugin : {}", plugin.name());
                plugin.start();
                logger.debug("Start plugin : {} succeeded", plugin.name());
                updateState(plugin, PluginState.STARTED);
            } else {
                logger.debug("skip start plugin, name: {}, state: {}", plugin.name(), plugin.state());
            }
        }
    }

    /**
     * Stops the started plugins.
     *
     * @throws PluginException if an error occurs during plugin stopping
     */
    synchronized public void stopPlugins() throws PluginException {
        logger.info("Stopping available plugins");
        for (Plugin plugin : plugins) {
            if (plugin.state() == PluginState.STARTED) {
                updateState(plugin, PluginState.STOPPING);
                logger.debug("Stop plugin : {}", plugin.name());
                plugin.stop();
                logger.debug("Stop plugin : {} succeeded", plugin.name());
                updateState(plugin, PluginState.STOPED);
            } else {
                logger.debug("skip stop plugin, name: {}, state: {}", plugin.name(), plugin.state());
            }
        }
    }

    private void updateState(Plugin plugin, PluginState state) {
        plugin.setState(state);
    }
}