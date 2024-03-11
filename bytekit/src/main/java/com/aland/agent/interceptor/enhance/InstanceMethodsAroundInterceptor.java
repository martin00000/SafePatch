package com.aland.agent.interceptor.enhance;

import com.aland.agent.plugin.PluginContext;

import java.lang.reflect.Method;

/**
 * InstanceMethodsAroundInterceptor
 * <p>
 * A interceptor, which intercept method's invocation.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/18
 */
public interface InstanceMethodsAroundInterceptor {

    /**
     * called before target method invocation.
     *
     * @param result change this result, if you want to truncate the method.
     * @throws Throwable
     */
    void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                      MethodInterceptResult result, PluginContext pluginContext) throws Throwable;

    /**
     * called after target method invocation. Even method's invocation triggers an exception.
     *
     * @param method
     * @param ret the method's original return value.
     * @return the method's actual return value.
     * @throws Throwable
     */
    Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                       Object ret, PluginContext pluginContext) throws Throwable;

    /**
     * called when occur exception.
     *
     * @param method
     * @param t the exception occur.
     */
    void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                               Class<?>[] argumentsTypes,
                               Throwable t, PluginContext pluginContext);
}
