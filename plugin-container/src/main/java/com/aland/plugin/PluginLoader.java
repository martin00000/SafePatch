package com.aland.plugin;

import java.io.PipedReader;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PluginLoader {


    protected static ClassLoader agentLibClassLoader = null;

    private Instrumentation instrumentation;

    private Properties properties;

    List<URL> pluginLocations = new ArrayList<>();



    public PluginLoader(Instrumentation instrumentation, Properties properties, URL pluginLocation) {
        this.instrumentation = instrumentation;
        this.properties = properties;
        this.pluginLocations.add(pluginLocation);
    }


    public static ClassLoader getAgentLibClassLoader() {
        return agentLibClassLoader;
    }

    public static void setAgentLibClassLoader(ClassLoader agentLibClassLoader) {
        PluginLoader.agentLibClassLoader = agentLibClassLoader;
    }



    /**
     * Loads the plugins.
     *
     * @return         	A list of Plugin objects.
     */
    synchronized public List<Plugin> load() {


        return null;

    }
}
