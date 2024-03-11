package com.aland.agent;

import com.aland.agent.match.ClassMatch;
import net.bytebuddy.dynamic.DynamicType;

/**
 * <p>
 * ByteKitPlugin is an interface used to define bytecode enhancement plugins.
 * It includes methods for defining and enhancing classes.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/23
 */
public interface ByteKitPlugin {


    /**
     * Defines a method for enhancing the specified class with bytecode.
     *
     * @param transformClassName The name of the class to be enhanced
     * @param builder            The bytecode builder
     * @param classLoader        The class loader
     * @param enhanceContext     The enhancement context
     * @return The enhanced bytecode builder
     * @throws BytekitException if an exception occurs during the bytecode enhancement process
     */
    DynamicType.Builder<?> define(String transformClassName,
                                  DynamicType.Builder<?> builder, ClassLoader classLoader, EnhanceContext enhanceContext) throws BytekitException;


    /**
     * Retrieves the class matcher for enhancing classes.
     *
     * @return The class matcher
     */
    ClassMatch enhanceClass();
}
