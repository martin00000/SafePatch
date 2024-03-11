package com.aland.agent.interceptor.enhance;

/**
 * EnhancedInstance
 * <p>
 *
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/18
 */
public interface EnhancedInstance {

    Object getSafePatchDynamicField();

    void setSafePatchDynamicField(Object value);
}
