package com.aland.agent.logger;

/**
 * <p>
 * Logger interface for logging messages with different log levels.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/26
 */
public interface Logger {
    // Log methods for trace level
    void trace(String msg);
    void trace(Throwable e);
    void trace(String msg, Throwable e);

    // Log methods for debug level
    void debug(String msg);
    void debug(Throwable e);
    void debug(String format, Object... argArray);
    void debug(String format, Object arg);
    void debug(String format, Object arg1, Object arg2);
    void debug(String msg, Throwable e);

    // Log methods for info level
    void info(String msg);
    void info(String format, Object arg);
    void info(String format, Object arg1, Object arg2);
    void info(String format, Object... argArray);
    void info(Throwable e);
    void info(String msg, Throwable e);

    // Log methods for warn level
    void warn(String msg);
    void warn(String format, Object arg);
    void warn(String format, Object... argArray);
    void warn(String format, Object arg1, Object arg2);
    void warn(Throwable e);
    void warn(String msg, Throwable e);

    // Log methods for error level
    void error(String msg);
    void error(String format, Object arg);
    void error(String format, Object arg1, Object arg2);
    void error(String format, Object... argArray);
    void error(Throwable e);
    void error(String msg, Throwable e);

    // Methods to check if different log levels are enabled
    boolean isTraceEnabled();
    boolean isDebugEnabled();
    boolean isInfoEnabled();
    boolean isWarnEnabled();
    boolean isErrorEnabled();
}