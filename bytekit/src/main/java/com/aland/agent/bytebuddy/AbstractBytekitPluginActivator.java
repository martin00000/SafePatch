package com.aland.agent.bytebuddy;

import com.aland.agent.plugin.PluginActivator;
import com.aland.agent.plugin.PluginContext;

import java.lang.instrument.Instrumentation;

/**
 * AbstractBytekitPluginActivator
 * <p>
 * AbstractBytekitPluginActivator is a Java abstract class that implements the PluginActivator interface.
 * It provides a basic implementation for the PluginActivator interface methods and defines abstract methods
 * that need to be implemented by the subclasses
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/15
 */
public abstract class AbstractBytekitPluginActivator implements PluginActivator {
    protected BytebuddyInstrumetation bytebuddyInstrumetation = null;

    /**
     * Returns true to enable the plugin.
     *
     * @param context the PluginContext object
     * @return true to enable the plugin
     */
    @Override
    public boolean enabled(PluginContext context) {
        return true;
    }

    /**
     * Initializes the plugin by creating a new instance of BytebuddyInstrumetation and installing it.
     *
     * @param context the PluginContext object
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void init(PluginContext context) throws Exception {
        bytebuddyInstrumetation = new BytebuddyInstrumetation();
        bytebuddyInstrumetation.install(context.getInstrumentation());
        init0(context);
    }

    /**
     * Abstract method that needs to be implemented by the subclasses to perform additional initialization.
     *
     * @param context the PluginContext object
     */
    abstract protected void init0(PluginContext context);

    /**
     * Starts the plugin.
     *
     * @param context the PluginContext object
     * @throws Exception if an error occurs during starting the plugin
     */
    @Override
    public void start(PluginContext context) throws Exception {
        Instrumentation instrumentation = context.getInstrumentation();
        start0(context);
        bytebuddyInstrumetation.startInstrumentation(instrumentation, context);
    }

    /**
     * Abstract method that needs to be implemented by the subclasses to perform additional startup tasks.
     *
     * @param context the PluginContext object
     */
    protected abstract void start0(PluginContext context);

    /**
     * Stops the plugin.
     *
     * @param context the PluginContext object
     * @throws Exception if an error occurs during stopping the plugin
     */
    @Override
    public void stop(PluginContext context) throws Exception {
        // Implementation for stopping the plugin
    }
}
