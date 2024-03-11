package com.aland.agent.interceptor.enhance;

import com.aland.agent.plugin.PluginContext;

/**
 * InstanceConstructorInterceptor
 * <p>
 * The instance constructor's interceptor interface.
 * Any plugin, which wants to intercept constructor, must implement this interface.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/18
 */
public interface InstanceConstructorInterceptor {
    /**
     * Called before the origin constructor invocation.
     */
    void onConstruct(EnhancedInstance objInst, Object[] allArguments, PluginContext pluginContext);
}
