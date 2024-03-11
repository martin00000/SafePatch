
package com.aland.agent.plugin.logger.slf4j;

import com.aland.agent.logger.Level;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerAdapter;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * <p>
 * Slf4jLoggerAdapter is a logger adapter implementation for the SLF4J logging framework.
 * It provides integration between the SLF4J logging framework and the agent logger.
 * The adapter allows the agent to use SLF4J as the underlying logging framework.
 * It initializes the SLF4J logger factory and configures the logging framework based on the agent's configuration.
 * The adapter also provides methods to get and set the logging level and file for the SLF4J logger.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/26
 **/
public class Slf4jLoggerAdapter implements LoggerAdapter {
    // Constants
    public static final String NAME = "slf4j";

    // Instance variables
    private Level level;
    private File file;
    private static final org.slf4j.Logger ROOT_LOGGER = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    // Static initialization block
    static {
        // Get the agent's log configuration file path
        String logConfigFilePath = System.getProperty("agent.home");

        try {
            // Get the SLF4J logger factory
            ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
            Class<? extends ILoggerFactory> classType = iLoggerFactory.getClass();

            // Configure Log4j if it is the active logging framework
            if (classType.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {
                Class<?> configurator;
                Object autoconfiguration;
                configurator = Class.forName("org.apache.log4j.xml.DOMConfigurator");
                autoconfiguration = configurator.newInstance();

                // Configure Log4j using the default configuration file or the agent's configuration file
                if (null == logConfigFilePath) {
                    Method configure = autoconfiguration.getClass().getMethod("configure", URL.class);
                    URL url = Slf4jLoggerAdapter.class.getClassLoader().getResource("log4j.xml");
                    configure.invoke(autoconfiguration, url);
                } else {
                    Method configure = autoconfiguration.getClass().getMethod("configure", String.class);
                    configure.invoke(autoconfiguration, logConfigFilePath + "/conf/log4j.xml");
                }
            }
            // Configure Logback if it is the active logging framework
            else if (classType.getName().equals("ch.qos.logback.classic.LoggerContext")) {
                Class<?> joranConfigurator;
                Class<?> context = Class.forName("ch.qos.logback.core.Context");
                Object joranConfiguratoroObj;
                joranConfigurator = Class.forName("ch.qos.logback.classic.joran.JoranConfigurator");
                joranConfiguratoroObj = joranConfigurator.newInstance();

                // Reset the logger context and configure Logback using the default configuration file or the agent's configuration file
                Method reset = iLoggerFactory.getClass().getMethod("reset");
                reset.invoke(iLoggerFactory);
                Method setContext = joranConfiguratoroObj.getClass().getMethod("setContext", context);
                setContext.invoke(joranConfiguratoroObj, iLoggerFactory);

                if (null == logConfigFilePath) {
                    URL url = Slf4jLoggerAdapter.class.getClassLoader().getResource("logback.xml");
                    Method doConfigure = joranConfiguratoroObj.getClass().getMethod("doConfigure", URL.class);
                    doConfigure.invoke(joranConfiguratoroObj, url);
                } else {
                    Method doConfigure = joranConfiguratoroObj.getClass().getMethod("doConfigure", String.class);
                    doConfigure.invoke(joranConfiguratoroObj, logConfigFilePath + "/conf/logback.xml");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LoggerAdapter interface methods
    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        System.err.printf(
                "The level of slf4j logger current can not be set, using the default level: %s \n",
                Slf4jLogger.getLevel(ROOT_LOGGER));
        this.level = level;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }
}