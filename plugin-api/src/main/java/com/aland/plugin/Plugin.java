package com.aland.plugin;

import java.net.URL;

public interface Plugin {

    /**
     * 让插件本身判断是否要启动
     */
    boolean enabled() throws PluginException;

    /**
     * Initializes the function.
     *
     * @throws PluginException  if an error occurs during initialization
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
     * @throws PluginException  If an error occurs during the execution of the function.
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
