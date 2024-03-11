package com.aland.agent;

import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.utils.StringUtils;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaConstant;

/**
 * AbstractClassEnhancePluginDefine
 * <p>
 * It provides the outline of enhancing the target class.
 * If you want to know more about enhancing, you should go to see
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public abstract class AbstractClassEnhancePluginDefine {
    private final Logger logger = LoggerFactory.getLogger(AbstractClassEnhancePluginDefine.class);

    public DynamicType.Builder<?> define(String transformClassName, DynamicType.Builder<?> builder, ClassLoader classLoader, EnhanceContext context) throws BytekitException {
        String interceptorDefineClassName = this.getClass().getName();

        if (StringUtils.isEmpty(transformClassName)) {
            logger.warn("classname of being intercepted is not defined by {}.", interceptorDefineClassName);
            return null;
        }

        logger.debug("prepare to enhance class {} by {}.", transformClassName, interceptorDefineClassName);

        /*
          find witness classes for enhance class
         */
        String[] witnessClasses = witnessClasses();
        if (witnessClasses != null) {
            for (String witnessClass : witnessClasses) {
                if (!WitnessClassFinder.INSTANCE.exist(witnessClass, classLoader)) {
                    logger.warn("enhance class {} by plugin {} is not working. Because witness class {} is not existed.", transformClassName, interceptorDefineClassName,
                            witnessClass);
                    return null;
                }
            }
        }

        /*
          find origin class source code for interceptor
         */
        DynamicType.Builder<?> newClassBuilder = this.enhance(transformClassName, builder, classLoader, context);
        context.initializationStageCompleted();
        logger.debug("enhance class {} by {} completely.", transformClassName, interceptorDefineClassName);

        return newClassBuilder;

    }

    protected abstract DynamicType.Builder<?> enhance(String enhanceOriginClassName,
                                                      DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) throws BytekitException;

    protected String[] witnessClasses() {
        return new String[]{};
    }


}