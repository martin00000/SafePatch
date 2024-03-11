package com.aland.agent.interceptor.enhance;

import com.aland.agent.BytekitException;

/**
 * EnhanceException
 * <p>
 * Define enhance exception.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/17
 */
public class EnhanceException extends BytekitException {
    public EnhanceException(String message) {
        super(message);
    }

    public EnhanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
