package com.aland.agent.logger;

import java.io.File;

/**
 * <p>
 * LoggerAdapter is an interface that defines the specification for a logger adapter.
 * It provides methods to get a logger, set the logging level, and set the logging file.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/26
 */
public interface LoggerAdapter {
    /**
     * Get a logger with the specified class name.
     *
     * @param key The class name used to name the returned logger
     * @return The logger
     */
    Logger getLogger(Class<?> key);

    /**
     * Get a logger with the specified name.
     *
     * @param key The name used to name the returned logger
     * @return The logger
     */
    Logger getLogger(String key);

    /**
     * Get the current logging level.
     *
     * @return The current logging level
     */
    Level getLevel();

    /**
     * Set the current logging level.
     *
     * @param level The logging level
     */
    void setLevel(Level level);

    /**
     * Get the current logging file.
     *
     * @return The current logging file
     */
    File getFile();

    /**
     * Set the current logging file.
     *
     * @param file The logging file
     */
    void setFile(File file);
}