package com.aland.agent.plugin;

import java.net.URL;

/**
 * <p>
 * This file contains the Plugin interface which defines the common methods for a plugin.
 * It is written in the Java programming language.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/26
 **/
public interface Plugin {

    /**
     * Checks if the plugin is enabled.
     *
     * @return true if the plugin is enabled, false otherwise
     * @throws PluginException if an error occurs during the check
     */
    boolean enabled() throws PluginException;

    /**
     * Initializes the function.
     *
     * @throws PluginException if an error occurs during initialization
     */
    void init() throws PluginException;

    /**
     * Starts the execution of the function.
     *
     * @throws PluginException if there is an exception during execution
     */
    void start() throws PluginException;

    /**
     * Retrieves the order of the plugin.
     *
     * @return the order of the plugin
     */
    int order();

    /**
     * Stops the execution of the function and throws a PluginException.
     *
     * @throws PluginException if an error occurs during the execution of the function
     */
    void stop() throws PluginException;

    /**
     * Retrieves the state of the plugin.
     *
     * @return the plugin state
     */
    PluginState state();

    /**
     * Sets the state of the plugin.
     *
     * @param state the state to be set
     */
    void setState(PluginState state);

    /**
     * Get the name of the object.
     *
     * @return the name of the object
     */
    String name();

    /**
     * Retrieves the location URL.
     *
     * @return the URL representing the location
     */
    URL location();
}