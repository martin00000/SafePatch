/**
 * <p>
 * This file contains the Level enum, which represents different levels of logging.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/26
 */
package com.aland.agent.logger;

/**
 * <p>
 * This file contains the Level enum, which represents different levels of logging.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/23
 */
public enum Level {

    /**
     * Represents all log levels.
     */
    ALL,
    /**
     * Represents the trace log level.
     */
    TRACE,

    /**
     * Represents the debug log level.
     */
    DEBUG,

    /**
     * Represents the info log level.
     */
    INFO,

    /**
     * Represents the warn log level.
     */
    WARN,

    /**
     * Represents the error log level.
     */
    ERROR,

    /**
     * Represents the off log level.
     */
    OFF
}
