package com.aland.agent.plugin;

/**
 * <p>
 * PluginException is a custom exception class that extends the Exception class.
 * It provides three constructors to create different types of PluginException objects.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/29
 **/
public class PluginException extends Exception {

    private static final long serialVersionUID = 1L;

    public PluginException() {
        super();
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
