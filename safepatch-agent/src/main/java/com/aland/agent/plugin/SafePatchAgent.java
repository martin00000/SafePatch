package com.aland.agent.plugin;

import com.aland.agent.loader.AgentClassLoader;
import com.aland.agent.logger.LoggerAdapter;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.properties.AgentConfig;
import com.aland.agent.utils.FileUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * SafePatchAgent
 * <p>
 * The main entrance of safe-patch agent, based on javaagent mechanism.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/29
 */
public class SafePatchAgent {
    private static final String JAGENT_HOME = "jagent.home";
    private static final boolean agentLoaded = false;
    private static PluginManager pluginManager;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        if (agentLoaded) {
            return;
        }

        try {
            setAgentHome();
        } catch (URISyntaxException e) {
            System.out.println("set agent home error, error: " + e.getMessage());
            return;
        }
        // init agent
        init(agentArgs, instrumentation);


    }

    private static void init(String agentArgs, Instrumentation instrumentation) {
        String agentHome = System.getProperty(JAGENT_HOME);
        Properties properties = new Properties();
        properties.put(JAGENT_HOME, agentHome);

        AgentConfig.AGENT_HOME = agentHome;
        AgentConfig.Agent.AGENT_APPLICATION_NAME = System.getProperty("applicationName");
        AgentConfig.Agent.IS_OPEN_DEBUGGING_CLASS = "true".equals(System.getProperty("jagent.openDumpClass"));

        AgentLibClassLoader agentLibClassLoader = new AgentLibClassLoader(FileUtils.listURL(new File(agentHome + "/lib")), SafePatchAgent.class.getClassLoader());
        // init logger adapter
        try {
            LoggerAdapter loggerAdapter = getServiceInstance(LoggerAdapter.class, agentLibClassLoader);
            LoggerFactory.setLoggerAdapter(loggerAdapter);
        } catch (Exception ex) {
            System.out.println("set logger adapter error, error: " + ex.getMessage());
            return;
        }

        // init plugin container
        try {
            pluginManager = new PluginManager(instrumentation, properties, new File(agentHome + "/plugins").toURI().toURL());
            pluginManager.loadPlugins();
            pluginManager.enablePlugins(Collections.emptyList());
            pluginManager.initPlugins();
            pluginManager.startPlugins();
        } catch (MalformedURLException | PluginException e) {
            System.out.println("init plugin manager error, error: " + e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread("safepatch-agent-shutdown-hook") {
            @Override
            public void run() {
                if (pluginManager != null) {
                    try {
                        pluginManager.stopPlugins();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });

    }

    private static <S> S getServiceInstance(Class<S> service, ClassLoader classLoader) {
        Iterator<S> iterator = ServiceLoader.load(service, classLoader).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    private static void setAgentHome() throws URISyntaxException {
        File agentFile = getAgentJarFile();
        String agentHome = agentFile.getParent();
        System.setProperty(JAGENT_HOME, agentHome);
        System.out.println("Original agent home is " + agentHome);

    }

    private static File getAgentJarFile() throws URISyntaxException {
        final File agentJar = new File(SafePatchAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (!agentJar.getName().endsWith(".jar")) {
            throw new IllegalStateException("Agent is not a jar file: " + agentJar);
        }
        return agentJar.getAbsoluteFile();
    }


}
