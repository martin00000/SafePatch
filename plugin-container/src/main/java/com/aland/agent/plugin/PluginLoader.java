package com.aland.agent.plugin;

import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.*;

/**
 * <p>
 * PluginLoader class is responsible for loading plugins in the application.
 * It scans the specified plugin location, retrieves the plugin files, and creates instances of the plugins.
 * The plugins are then sorted based on their order and returned as a list.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/25
 **/
public class PluginLoader {
    private final Instrumentation instrumentation;
    private final Properties properties;
    List<URL> pluginLocations = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(PluginLoader.class);

    /**
     * Constructor to set the instrumentation, properties, and plugin location.
     * @param instrumentation The instrumentation object.
     * @param properties The properties object.
     * @param pluginLocation The URL of the plugin location.
     */
    public PluginLoader(Instrumentation instrumentation, Properties properties, URL pluginLocation) {
        this.instrumentation = instrumentation;
        this.properties = properties;
        this.pluginLocations.add(pluginLocation);
    }

    /**
     * Loads the plugins by scanning the specified plugin locations.
     * Creates instances of the plugins and adds them to the list.
     * Sorts the plugins based on their order and returns the final list.
     *
     * @return A list of loaded plugins.
     * @throws PluginException If there is an error while scanning or creating the plugins.
     */
    synchronized public List<Plugin> load() throws PluginException {
        List<Plugin> plugins = new ArrayList<>();
        List<File> pluginsRoot = getFiles();
        for (File file : pluginsRoot) {
            try {
                //TODO 暂时不区分jar
                AgentPlugin plugin = new AgentPlugin(file.toURI().toURL(), instrumentation, properties);
                if (!containsPlugin(plugin.name(), plugins)) {
                    plugins.add(plugin);
                    logger.debug("plugin {} scanned", plugin.name());
                }
            } catch (Exception e) {
                throw new PluginException("scan plugins error.", e);
            }
        }
        plugins.sort((o1, o2) -> o1.order() - o2.order());
        return plugins;
    }

    /**
     * Scans the plugin locations and returns a list of plugin files.
     *
     * @return A list of plugin files.
     * @throws PluginException If there is an error while scanning the plugin locations.
     */
    private List<File> getFiles() throws PluginException {
        List<File> pluginsRoot = new ArrayList<>();
        for (URL scanLocation : pluginLocations) {
            File dir = new File(scanLocation.getFile());
            try {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.isHidden() && file.isDirectory()) {
                            pluginsRoot.add(file);
                        }
                    }
                }
            } catch (Exception e) {
                throw new PluginException("scan plugins error.", e);
            }
        }
        return pluginsRoot;
    }

    /**
     * Checks if the given plugin name already exists in the list of loaded plugins.
     *
     * @param name    The name of the plugin to check.
     * @param plugins The list of loaded plugins.
     * @return True if the plugin with the given name already exists, false otherwise.
     */
    synchronized public boolean containsPlugin(String name, List<Plugin> plugins) {
        for (Plugin plugin : plugins) {
            if (plugin.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
