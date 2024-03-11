package com.aland.agent.interceptor.enhance;

import com.aland.agent.BytekitException;
import com.aland.agent.loader.InterceptorInstanceLoader;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.plugin.PluginContext;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * InstMethodsInterWithOverrideArgs
 * <p>
 * The actual byte-buddy's interceptor to intercept class instance methods.
 * In this class, it provide a bridge between byte-buddy and this plugin.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public class InstMethodsInterWithOverrideArgs {
    private final Logger logger = LoggerFactory.getLogger(InstMethodsInterWithOverrideArgs.class);


    /**
     * An {@link InstanceMethodsAroundInterceptor}
     * This name should only stay in {@link String}, the real {@link Class} type will trigger classloader failure.
     * If you want to know more, please check on books about Classloader or Classloader appointment mechanism.
     */
    private InstanceMethodsAroundInterceptor interceptor;

    private PluginContext pluginContext;

    /**
     * @param instanceMethodsAroundInterceptorClassName class full name.
     */
    public InstMethodsInterWithOverrideArgs(String instanceMethodsAroundInterceptorClassName, ClassLoader classLoader, PluginContext pluginContext) {
        try {
            interceptor = InterceptorInstanceLoader.load(instanceMethodsAroundInterceptorClassName, classLoader);
        } catch (Throwable t) {
            throw new BytekitException("Can't create InstanceMethodsAroundInterceptor.", t);
        }
        this.pluginContext = pluginContext;
    }


    /**
     * Intercept the target instance method.
     *
     * @param obj          target class instance.
     * @param allArguments all method arguments
     * @param method       method description.
     * @param zuper        the origin call ref.
     * @return the return value of target instance method.
     * @throws Exception only throw exception because of zuper.call() or unexpected exception in sky-walking ( This is a
     *                   bug, if anything triggers this condition ).
     */
    @RuntimeType
    public Object intercept(@This Object obj,
                            @AllArguments Object[] allArguments,
                            @Origin Method method,
                            @Morph OverrideCallable zuper
    ) throws Throwable {

        EnhancedInstance targetObject = (EnhancedInstance) obj;
        MethodInterceptResult result = new MethodInterceptResult();

        try {
            interceptor.beforeMethod(targetObject, method, allArguments, method.getParameterTypes(),
                    result, pluginContext);
        } catch (Throwable t) {
            logger.error("class[{}] before method[{}] intercept failure", obj.getClass(), method.getName());
        }

        Object ret = null;
        try {
            if (!result.isContinue()) {
                ret = result._ret();
            } else {
                ret = zuper.call(allArguments);
            }
        } finally {
            try {
                ret = interceptor.afterMethod(targetObject, method, allArguments, method.getParameterTypes(),
                        ret, pluginContext);
            } catch (Throwable t) {
                logger.error("class[{}] after method[{}] intercept failure", obj.getClass(), method.getName());
            }

        }
        return ret;
    }


}
