package com.aland.agent.interceptor.enhance;

import com.aland.agent.plugin.PluginContext;

import java.lang.reflect.Method;

/**
 * StaticMethodsAroundInterceptor
 * <p>
 * The static method's interceptor interface.
 * Any plugin, which wants to intercept static methods, must implement this interface.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public interface StaticMethodsAroundInterceptor {

    /**
     * called before target method invocation.
     *
     * @param method
     * @param result change this result, if you want to truncate the method.
     */
    void beforeMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes,
                      MethodInterceptResult result , PluginContext pluginContext);

    /**
     * called after target method invocation. Even method's invocation triggers an exception.
     *
     * @param method
     * @param ret the method's original return value.
     * @return the method's actual return value.
     */
    Object afterMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, Object ret,PluginContext pluginContext);

    /**
     * called when occur exception.
     *
     * @param method
     * @param t the exception occur.
     */
    void handleMethodException(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes,
                               Throwable t,PluginContext pluginContext);
}
