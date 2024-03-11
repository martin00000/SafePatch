package com.aland.agent.interceptor.enhance;

import com.aland.agent.loader.InterceptorInstanceLoader;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.plugin.PluginContext;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;

import java.lang.reflect.Method;

/**
 * StaticMethodsInterWithOverrideArgs
 * <p>
 * TODO: Provide a brief summary of the class's purpose.
 * <p>
 * TODO: Provide a detailed description of the class, including its responsibilities,
 * how it should be used, and any other relevant information.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public class StaticMethodsInterWithOverrideArgs {
    Logger logger = LoggerFactory.getLogger(StaticMethodsInterWithOverrideArgs.class);

    private String staticMethodsAroundInterceptorClassName;

    private PluginContext pluginContext;

    public StaticMethodsInterWithOverrideArgs(String staticMethodsAroundInterceptorClassName, PluginContext pluginContext) {
        this.staticMethodsAroundInterceptorClassName = staticMethodsAroundInterceptorClassName;
        this.pluginContext = pluginContext;
    }


    public Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allarguments, @Origin Method method, @Morph OverrideCallable zuper) throws Throwable {
        StaticMethodsAroundInterceptor interceptor = InterceptorInstanceLoader.load(staticMethodsAroundInterceptorClassName, clazz.getClassLoader());

        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(clazz, method, allarguments, method.getParameterTypes(), result, pluginContext);
        } catch (Throwable t) {
            logger.error("class[{}] before static method[{}] intercept failure", t, clazz, method.getName());
        }

        Object ret = null;
        try {
            if (!result.isContinue()) {
                ret = result._ret();
            } else {
                ret = zuper.call(allarguments);
            }
        } finally {
            try {
                ret = interceptor.afterMethod(clazz, method, allarguments, method.getParameterTypes(), ret, pluginContext);
            } catch (Throwable t) {
                logger.error("class[{}] after static method[{}] intercept failure:{}", clazz, method.getName(), t.getMessage());
            }
        }
        return ret;
    }
}
