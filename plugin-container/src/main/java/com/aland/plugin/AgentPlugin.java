package com.aland.plugin;


import com.aland.agent.IOUtils;
import com.aland.agent.properties.PropertiesInjectUtil;
import com.aland.loader.AgentClassLoader;
import sun.plugin.security.PluginClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Class AgentPlugin
 * <p>
 * Created by aland on 2023/12/25.
 *
 * @version 1.0
 */
public class AgentPlugin implements Plugin {

//    private static final PLogger logger = PLoggerFactory.getLogger(PluginManager.class);


    private URL location;

    private PluginConfig pluginConfig;

    private volatile PluginState state;

    private PluginActivator pluginActivator;

    private PluginContext pluginContext;


    public AgentPlugin(URL location, Instrumentation instrumentation, Properties gobalProperties) throws PluginException {

        this.location = location;
        this.state = PluginState.NONE;

        Properties properties = new Properties();
        properties.putAll(gobalProperties);


        File file = new File(location.getPath() + "conf/agent-plugin.properties");
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new PluginException("load plugin properties error, file: " + file, e);
        }

        pluginConfig = new PluginConfig();
        PropertiesInjectUtil.inject(properties, pluginConfig);

        this.pluginContext = new PluginContextImpl(this, instrumentation, properties);
    }

    @Override
    public boolean enabled() throws PluginException {
        boolean enabled = false;
        try {
            Class<?> activatorClass = AgentClassLoader.getDefault().loadClass(pluginConfig.getPluginActivator());
            pluginActivator = (PluginActivator) activatorClass.newInstance();
            enabled = pluginActivator.enabled(pluginContext);
            if (enabled) {
                this.state = PluginState.ENABLED;
            } else {
                this.state = PluginState.DISABLED;
//                logger.info("plugin {} disabled.", this.pluginConfig.getName());
            }

        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("check enabled plugin error, plugin name: " + pluginConfig.getName(), e);
        }
        return enabled;
    }

    @Override
    public void init() throws PluginException {
        try {
            pluginActivator.init(pluginContext);
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("init plugin error, plugin name: " + pluginConfig.getName(), e);
        }
    }

    @Override
    public void start() throws PluginException {
        try {
            pluginActivator.start(pluginContext);
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("start plugin error, plugin name: " + pluginConfig.getName(), e);
        }
    }

    @Override
    public void stop() throws PluginException {
        try {
            pluginActivator.stop(pluginContext);
        } catch (Throwable e) {
            this.state = PluginState.ERROR;
            throw new PluginException("stop plugin error, plugin name: " + pluginConfig.getName(), e);
        }
    }



    @Override
    public String name() {
        return this.pluginConfig.getName();
    }

    @Override
    public PluginState state() {
        return this.state;
    }

    @Override
    public void setState(PluginState state) {
        this.state = state;
    }

    @Override
    public URL location() {
        return location;
    }

    @Override
    public int order() {
        return pluginConfig.getOrder();
    }

    private List<URL> scanPluginUrls() throws PluginException {
        File libDir = new File(location.getFile(), "lib");
        File[] listFiles = libDir.listFiles();
        List<URL> urls = new ArrayList<>();
        try {
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file.getName().endsWith(".jar")) {
                        urls.add(file.toURI().toURL());
                    }
                }
            }

            File confDir = new File(location.getFile(), "conf");
            if (confDir.isDirectory()) {
                urls.add(confDir.toURI().toURL());
            }
        } catch (MalformedURLException e) {
            throw new PluginException("", e);
        }

        return urls;
    }

}
