package com.aland.agent.interceptor.enhance;

/**
 * <p>
 The scenario of dynamically changing method parameters before the interception of method execution.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 *
 */
public interface OverrideCallable {
    Object call(Object[] args);
}
