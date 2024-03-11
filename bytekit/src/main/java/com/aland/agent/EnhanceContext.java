package com.aland.agent;

import com.aland.agent.plugin.PluginContext;

/**
 * <p>
 * EnhanceContext is a Java class that represents a context for enhancing or extending objects.
 * It contains methods to check if the object has been enhanced or extended, and to set the plugin context.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/23
 *
 */
public class EnhanceContext {
    private boolean isEnhanced = false;
    private boolean objectExtended = false;

    private PluginContext pluginContext = null;

    /**
     * Checks if the object has been enhanced.
     *
     * @return true if the object has been enhanced, false otherwise
     */
    public boolean isEnhanced() {
        return isEnhanced;
    }

    /**
     * Marks the initialization stage as completed, indicating that the object has been enhanced.
     */
    public void initializationStageCompleted() {
        isEnhanced = true;
    }

    /**
     * Checks if the object has been extended.
     *
     * @return true if the object has been extended, false otherwise
     */
    public boolean isObjectExtended() {
        return objectExtended;
    }

    /**
     * Marks the object extension as completed, indicating that the object has been extended.
     */
    public void extendObjectCompleted() {
        objectExtended = true;
    }

    /**
     * Gets the plugin context.
     *
     * @return the plugin context
     */
    public PluginContext getPluginContext() {
        return pluginContext;
    }

    /**
     * Sets the plugin context.
     *
     * @param pluginContext the plugin context to set
     */
    public void setPluginContext(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }


}
