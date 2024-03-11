package com.aland.agent.interceptor.enhance;

import com.aland.agent.AbstractClassEnhancePluginDefine;
import com.aland.agent.BytekitException;
import com.aland.agent.EnhanceContext;
import com.aland.agent.interceptor.ConstructorInterceptPoint;
import com.aland.agent.interceptor.InstanceMethodsInterceptPoint;
import com.aland.agent.interceptor.StaticMethodsInterceptPoint;
import com.aland.agent.utils.StringUtils;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * ClassEnhancePluginDefine
 * <p>
 * This class controls all enhance operations, including enhance constructors, instance methods and static methods
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public abstract class ClassEnhancePluginDefine extends AbstractClassEnhancePluginDefine {

    /**
     * New field name.
     */
    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";


    /**
     * Begin to define how to enhance class.
     * After invoke this method, only means definition is finished.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder        byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    @Override
    protected DynamicType.Builder<?> enhance(String enhanceOriginClassName,
                                             DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) throws BytekitException {
        newClassBuilder = this.enhanceClass(enhanceOriginClassName, newClassBuilder, classLoader, context);

        newClassBuilder = this.enhanceInstance(enhanceOriginClassName, newClassBuilder, classLoader, context);


        return newClassBuilder;
    }

    private DynamicType.Builder<?> enhanceInstance(String enhanceOriginClassName, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) {
        ConstructorInterceptPoint[] constructorInterceptPoints = getConstructorsInterceptPoints();
        InstanceMethodsInterceptPoint[] instanceMethodsInterceptPoints = getInstanceMethodsInterceptPoints();

        boolean existedConstructorInterceptPoint = constructorInterceptPoints != null && constructorInterceptPoints.length > 0;
        boolean existedMethodsInterceptPoints = instanceMethodsInterceptPoints != null && instanceMethodsInterceptPoints.length > 0;
        /*
          nothing need to be enhanced in class instance, maybe need enhance static methods.
         */
        if (!existedConstructorInterceptPoint && !existedMethodsInterceptPoints) {
            return newClassBuilder;
        }

        /**
         * Manipulate class source code.<br/>
         *
         * new class need:<br/>
         * 1.Add field, name {@link #CONTEXT_ATTR_NAME}.
         * 2.Add a field accessor for this field.
         *
         * And make sure the source codes manipulation only occurs once.
         *
         */
        if (!context.isObjectExtended()) {
            newClassBuilder = newClassBuilder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE)
                    .implement(EnhancedInstance.class)
                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
            context.extendObjectCompleted();
        }

        /**
         * 2. enhance constructors
         */
        if (existedConstructorInterceptPoint) {
            for (ConstructorInterceptPoint constructorInterceptPoint : constructorInterceptPoints) {
                newClassBuilder = newClassBuilder.constructor(constructorInterceptPoint.getConstructorMatcher()).intercept(SuperMethodCall.INSTANCE
                        .andThen(MethodDelegation.withDefaultConfiguration()
                                .to(new ConstructorInter(constructorInterceptPoint.getConstructorInterceptor(), classLoader, context.getPluginContext()))
                        )
                );
            }
        }

        /**
         * 3. enhance instance methods
         */
        if (existedMethodsInterceptPoints) {
            for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint : instanceMethodsInterceptPoints) {
                String interceptor = instanceMethodsInterceptPoint.getMethodsInterceptor();
                if (StringUtils.isEmpty(interceptor)) {
                    throw new EnhanceException("no InstanceMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
                }

                if (instanceMethodsInterceptPoint.isOverrideArgs()) {
                    newClassBuilder =
                            newClassBuilder.method(not(isStatic()).and(instanceMethodsInterceptPoint.getMethodsMatcher()))
                                    .intercept(
                                            MethodDelegation.withDefaultConfiguration()
                                                    .withBinders(
                                                            Morph.Binder.install(OverrideCallable.class)
                                                    )
                                                    .to(new InstMethodsInterWithOverrideArgs(interceptor, classLoader, context.getPluginContext()))
                                    );
                } else {
                    newClassBuilder =
                            newClassBuilder.method(not(isStatic()).and(instanceMethodsInterceptPoint.getMethodsMatcher()))
                                    .intercept(
                                            MethodDelegation.withDefaultConfiguration()
                                                    .to(new InstMethodsInter(interceptor, classLoader, context.getPluginContext()))
                                    );
                }
            }
        }
        return newClassBuilder;
    }


    protected abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();

    protected abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();


    /**
     * Enhance a class to intercept constructors and class instance methods.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder        byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    private DynamicType.Builder<?> enhanceClass(String enhanceOriginClassName,
                                                DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) throws BytekitException {
        StaticMethodsInterceptPoint[] staticMethodsInterceptPoints = getStaticMethodsInterceptPoints();
        if (staticMethodsInterceptPoints == null) {
            return newClassBuilder;
        }

        for (StaticMethodsInterceptPoint staticMethodsInterceptPoint : staticMethodsInterceptPoints) {
            String methodsInterceptor = staticMethodsInterceptPoint.getMethodsInterceptor();
            if (StringUtils.isEmpty(methodsInterceptor)) {
                throw new EnhanceException("no StaticMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
            }

            if (staticMethodsInterceptPoint.isOverrideArgs()) {
                newClassBuilder = newClassBuilder.method(isStatic().and(staticMethodsInterceptPoint.getMethodsMatcher()))
                        .intercept(MethodDelegation.withDefaultConfiguration().withBinders(Morph.Binder.install(OverrideCallable.class))
                                .to(new StaticMethodsInterWithOverrideArgs(methodsInterceptor, context.getPluginContext())));
            } else {
                newClassBuilder = newClassBuilder.method(isStatic().and(staticMethodsInterceptPoint.getMethodsMatcher()))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new StaticMethodsInter(methodsInterceptor, context.getPluginContext())));
            }
        }

        return newClassBuilder;
    }

    protected abstract StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints();
}
