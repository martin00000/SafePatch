package com.aland.agent.plugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * <p>
 * AgentLibClassLoader is a class that extends URLClassLoader in Java.
 * It is used to load classes from specific URLs with a given parent ClassLoader.
 * This class overrides the loadClass method to provide custom class loading behavior.
 * The class also registers itself as parallel capable if possible.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/25
 **/
public class AgentLibClassLoader extends URLClassLoader {
    static {
        if (!ClassLoader.registerAsParallelCapable()) {
            System.err.println(" AgentLibClassLoader::registerAsParallelCapable() fail");
        }
    }

    /**
     * Break the default parent delegation rule of the ClassLoader to prioritize loading its own classes, addressing the issues of
     * 1. class contamination and
     * 2. potential inconsistencies with parent versions.
     *
     * @param urls   the URLs from which to load classes and resources
     * @param parent the parent ClassLoader
     */
    public AgentLibClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }
}