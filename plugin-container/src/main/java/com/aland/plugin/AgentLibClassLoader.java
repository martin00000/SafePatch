package com.aland.plugin;

import java.net.URL;
import java.net.URLClassLoader;


/**
 * 插件类加载器
 */
public class AgentLibClassLoader extends URLClassLoader {

    static {
        if (!ClassLoader.registerAsParallelCapable()) {
            System.err.println(" AgentLibClassLoader::registerAsParallelCapable() fail");
        }
    }

    public AgentLibClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }


    /**
     * 破坏ClassLoader默认双亲委托规则，优先加载自己的类，解决 1.类污染问题  2.父版本不一致可能存在的问题
     * @param name
     * @param resolve
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
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
