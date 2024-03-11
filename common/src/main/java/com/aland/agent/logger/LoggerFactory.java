package com.aland.agent.logger;

import com.aland.agent.utils.ConcurrentHashMapUtils;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * This class represents a logger factory that provides methods to set the logger provider,
 * get logger instances, and get the current logging file.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/26
 **/
public class LoggerFactory {
    private static final ConcurrentMap<String, Logger> LOGGERS = new ConcurrentHashMap<>();
    private static volatile LoggerAdapter loggerAdapter;

    private LoggerFactory() {
    }

    /**
     * Set logger provider
     *
     * @param loggerAdapter logger provider
     */
    public static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
        if (loggerAdapter != null) {
            if (loggerAdapter == LoggerFactory.loggerAdapter) {
                return;
            }
            loggerAdapter.getLogger(LoggerFactory.class.getName());
            LoggerFactory.loggerAdapter = loggerAdapter;
        }
    }

    /**
     * Get logger provider
     *
     * @param key the returned logger will be named after clazz
     * @return logger
     */
    public static Logger getLogger(Class<?> key) {
        return ConcurrentHashMapUtils.computeIfAbsent(
                LOGGERS, key.getName(), name -> loggerAdapter.getLogger(name));
    }

    /**
     * Get logger provider
     *
     * @param key the returned logger will be named after key
     * @return logger provider
     */
    public static Logger getLogger(String key) {
        return ConcurrentHashMapUtils.computeIfAbsent(
                LOGGERS, key, k -> loggerAdapter.getLogger(k));
    }

    /**
     * Get the current logging file
     *
     * @return current logging file
     */
    public static File getFile() {
        return loggerAdapter.getFile();
    }
}