package com.aland.agent.plugin;

import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.properties.PropertiesInjectUtil;
import com.aland.agent.loader.AgentClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

/**
 * <p>
 * Java code for the AgentPlugin class.
 * This class represents a plugin in an agent system.
 * It implements the Plugin interface and provides methods for enabling, initializing, starting, and stopping the plugin.
 * The plugin is configured using a properties file and can be in different states (ENABLED, DISABLED, ERROR).
 * It also provides information about the plugin's name, state, location, and order.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/25
 **/
public class AgentPlugin implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(AgentPlugin.class);
    private URL location;
    private PluginConfig pluginConfig;
    private volatile PluginState state;
    private PluginActivator pluginActivator;
    private PluginContext pluginContext;

    /**
     * Constructor for AgentPlugin.
     * Initializes the plugin with the provided location, instrumentation, and global properties.
     * Loads the plugin properties from a file and creates a PluginConfig object.
     * Creates a PluginContext object with the plugin, instrumentation, and properties.
     *
     * @param location        The location of the plugin.
     * @param instrumentation The instrumentation object.
     * @param gobalProperties The global properties.
     * @throws PluginException If there is an error loading the plugin properties.
     */
    public AgentPlugin(URL location, Instrumentation instrumentation, Properties gobalProperties) throws PluginException {
        this.location = location;
        this.state = PluginState.NONE;
        Properties properties = new Properties();
        properties.putAll(gobalProperties);
        File file = new File(location.getPath() + "conf/agent-plugin.properties");
        try {
            properties.load(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new PluginException("load plugin properties error, file: " + file, e);
        }
        pluginConfig = new PluginConfig();
        PropertiesInjectUtil.inject(properties, pluginConfig);
        this.pluginContext = new PluginContextImpl(this, instrumentation, properties);
    }

    /**
     * Checks if the plugin is enabled.
     * Loads the PluginActivator class and calls the enabled method on it.
     * Updates the plugin state based on the result.
     *
     * @return True if the plugin is enabled, false otherwise.
     * @throws PluginException If there is an error checking the enabled state of the plugin.
     */
    @Override
    public boolean enabled() throws PluginException {
        boolean enabled = false;
        try {
            Class<?> activatorClass = AgentClassLoader.getDefault().loadClass(pluginConfig.getPluginActivator());
            pluginActivator = (PluginActivator) activatorClass.newInstance();
            enabled = pluginActivator.enabled(pluginContext);
            if (enabled) {
                this.state = PluginState.ENABLED;
            } else {
                this.state = PluginState.DISABLED;
                logger.info("plugin {} disabled.", this.pluginConfig.getName());
            }
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("check enabled plugin error, plugin name: " + pluginConfig.getName(), e);
        }
        return enabled;
    }

    /**
     * Initializes the plugin.
     * Calls the init method on the PluginActivator.
     *
     * @throws PluginException If there is an error initializing the plugin.
     */
    @Override
    public void init() throws PluginException {
        try {
            pluginActivator.init(pluginContext);
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("init plugin error, plugin name: " + pluginConfig.getName(), e);
        }
    }

    /**
     * Starts the plugin.
     * Calls the start method on the PluginActivator.
     *
     * @throws PluginException If there is an error starting the plugin.
     */
    @Override
    public void start() throws PluginException {
        try {
            pluginActivator.start(pluginContext);
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("start plugin error, plugin name: " + pluginConfig.getName(), e);
        }
    }

    /**
     * Stops the plugin.
     * Calls the stop method on the PluginActivator.
     *
     * @throws PluginException If there is an error stopping the plugin.
     */
    @Override
    public void stop() throws PluginException {
        try {
            pluginActivator.stop(pluginContext);
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("stop plugin error, plugin name: " + pluginConfig.getName(), e);
        }
    }

    /**
     * Gets the name of the plugin.
     *
     * @return The name of the plugin.
     */
    @Override
    public String name() {
        return this.pluginConfig.getName();
    }

    /**
     * Gets the state of the plugin.
     *
     * @return The state of the plugin.
     */
    @Override
    public PluginState state() {
        return this.state;
    }

    /**
     * Sets the state of the plugin.
     *
     * @param state The state to set.
     */
    @Override
    public void setState(PluginState state) {
        this.state = state;
    }

    /**
     * Gets the location of the plugin.
     *
     * @return The location of the plugin.
     */
    @Override
    public URL location() {
        return location;
    }

    /**
     * Gets the order of the plugin.
     *
     * @return The order of the plugin.
     */
    @Override
    public int order() {
        return pluginConfig.getOrder();
    }
}