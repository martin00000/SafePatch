package com.aland.agent.plugin;

import com.aland.agent.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Properties;

/**
 * <p>
 * PluginContextImpl is a class that implements the PluginContext interface.
 * It provides methods to access the plugin, properties, and instrumentation.
 * The class also includes methods to convert a class to bytes and redefine a class.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/25
 **/
public class PluginContextImpl implements PluginContext {
    private Plugin plugin;
    private Properties properties;
    private Instrumentation instrumentation;

    /**
     * Constructor for PluginContextImpl.
     * Initializes the plugin, instrumentation, and properties.
     *
     * @param plugin          The plugin instance.
     * @param instrumentation The instrumentation instance.
     * @param properties      The properties for the plugin.
     */
    public PluginContextImpl(Plugin plugin, Instrumentation instrumentation, Properties properties) {
        this.plugin = plugin;
        this.instrumentation = instrumentation;
        this.properties = properties;
    }

    /**
     * Get the plugin instance.
     *
     * @return The plugin instance.
     */
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get the value of a property by key.
     *
     * @param key The key of the property.
     * @return The value of the property.
     */
    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get the instrumentation instance.
     *
     * @return The instrumentation instance.
     */
    @Override
    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    /**
     * Convert a class to bytes.
     *
     * @param theClass The class to convert.
     * @return The byte array representation of the class.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public byte[] toBytes(Class<?> theClass) throws IOException {
        String resource = theClass.getName().replace('.', '/') + ".class";
        InputStream is = theClass.getClassLoader().getResourceAsStream(resource);
        return IOUtils.toByteArray(is);
    }

    /**
     * Redefine a class with new bytecode.
     *
     * @param theClass     The class to redefine.
     * @param theClassFile The new bytecode of the class.
     * @throws ClassNotFoundException     If the class cannot be found.
     * @throws UnmodifiableClassException If the class cannot be modified.
     */
    @Override
    public void redefine(Class<?> theClass, byte[] theClassFile) throws ClassNotFoundException, UnmodifiableClassException {
        ClassDefinition classDefinition = new ClassDefinition(theClass, theClassFile);
        getInstrumentation().redefineClasses(classDefinition);
    }
}