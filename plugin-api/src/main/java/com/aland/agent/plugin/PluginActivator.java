package com.aland.agent.plugin;

/**
 * <p>
 * This is the PluginActivator interface.
 * It defines the methods that need to be implemented by a plugin activator.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/29
 **/
public interface PluginActivator {
    /**
     * Checks if the plugin is enabled.
     *
     * @param context The plugin context.
     * @return True if the plugin is enabled, false otherwise.
     */
    boolean enabled(PluginContext context);

    /**
     * Initializes the plugin.
     *
     * @param context The plugin context.
     * @throws Exception If an error occurs during initialization.
     */
    void init(PluginContext context) throws Exception;

    /**
     * Starts the plugin.
     *
     * @param context The plugin context.
     * @throws Exception If an error occurs during startup.
     */
    void start(PluginContext context) throws Exception;

    /**
     * Stops the plugin.
     *
     * @param context The plugin context.
     * @throws Exception If an error occurs during shutdown.
     */
    void stop(PluginContext context) throws Exception;
}