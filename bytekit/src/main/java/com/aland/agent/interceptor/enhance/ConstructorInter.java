package com.aland.agent.interceptor.enhance;

import com.aland.agent.BytekitException;
import com.aland.agent.loader.InterceptorInstanceLoader;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.plugin.PluginContext;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * ConstructorInter
 * <p>
 * The actual byte-buddy's interceptor to intercept constructor methods.
 * In this class, it provide a bridge between byte-buddy and  plugin.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/18
 */
public class ConstructorInter {
    private Logger logger = LoggerFactory.getLogger(ConstructorInter.class);

    /**
     * An {@link InstanceConstructorInterceptor}
     * This name should only stay in {@link String}, the real {@link Class} type will trigger classloader failure.
     * If you want to know more, please check on books about Classloader or Classloader appointment mechanism.
     */
    private InstanceConstructorInterceptor interceptor;

    private PluginContext pluginContext;

    /**
     * @param constructorInterceptorClassName class full name.
     */
    public ConstructorInter(String constructorInterceptorClassName, ClassLoader classLoader, PluginContext pluginContext) throws BytekitException {
        try {
            interceptor = InterceptorInstanceLoader.load(constructorInterceptorClassName, classLoader);
        } catch (Throwable t) {
            throw new BytekitException("Can't create InstanceConstructorInterceptor.", t);
        }

        this.pluginContext = pluginContext;
    }

    /**
     * Intercept the target constructor.
     *
     * @param obj target class instance.
     * @param allArguments all constructor arguments
     */
    @RuntimeType
    public void intercept(@This Object obj,
                          @AllArguments Object[] allArguments) {
        try {
            EnhancedInstance targetObject = (EnhancedInstance)obj;

            interceptor.onConstruct(targetObject, allArguments,pluginContext);
        } catch (Throwable t) {
            logger.error("ConstructorInter failure.", t);
        }

    }

}
