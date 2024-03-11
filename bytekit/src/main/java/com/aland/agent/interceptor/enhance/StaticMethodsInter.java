package com.aland.agent.interceptor.enhance;

import com.aland.agent.loader.InterceptorInstanceLoader;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.plugin.PluginContext;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * StaticMethodsInter
 * <p>
 * The actual byte-buddy's interceptor to intercept class instance methods.
 * In this class, it provide a bridge between byte-buddy and this plugin.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public class StaticMethodsInter {
    private final Logger logger = LoggerFactory.getLogger(StaticMethodsInter.class);

    private String staticMethodsAroundInterceptorClassName;


    private PluginContext pluginContext;


    public StaticMethodsInter(String staticMethodsAroundInterceptorClassName, PluginContext pluginContext) {
        this.staticMethodsAroundInterceptorClassName = staticMethodsAroundInterceptorClassName;
        this.pluginContext = pluginContext;
    }

    @RuntimeType
    public Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments, @Origin Method method,
                            @SuperCall Callable<?> zuper) throws Throwable {
        StaticMethodsAroundInterceptor interceptor = InterceptorInstanceLoader.load(staticMethodsAroundInterceptorClassName, clazz.getClassLoader());

        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(clazz, method, allArguments, method.getParameterTypes(), result, pluginContext);
        } catch (Throwable t) {
            logger.error("class[{}] before static method[{}] intercept failure", t, clazz, method.getName());
        }

        Object ret = null;
        try {
            if (!result.isContinue()) {
                ret = result._ret();
            } else {
                ret = zuper.call();
            }
        } finally {
            try {
                ret = interceptor.afterMethod(clazz, method, allArguments, method.getParameterTypes(), ret, pluginContext);
            } catch (Throwable t) {
                logger.error("class[{}] after static method[{}] intercept failure:{}", clazz, method.getName(), t.getMessage());
            }
        }
        return ret;
    }

}
