package com.aland.agent.loader;


import com.aland.agent.ByteKitPlugin;
import com.aland.agent.BytekitException;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;


import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>
 * AgentClassLoader is a class loader implementation in Java.
 * It extends the ClassLoader class and is used to load classes and resources from jars and directories.
 * The class provides methods for finding classes, resources, and loading bytecode.
 * It also handles the registration of the class loader as parallel capable to avoid classloader deadlock.
 * The class maintains a list of jars and directories in the classpath, and scans them to find classes and resources.
 * It also provides a default class loader for the agent.* AgentClassLoader is a class loader implementation in Java.
 * It extends the ClassLoader class and is used to load classes and resources from jars and directories.
 * The class provides methods for finding classes, resources, and loading bytecode.
 * It also handles the registration of the class loader as parallel capable to avoid classloader deadlock.
 * The class maintains a list of jars and directories in the classpath, and scans them to find classes and resources.
 * It also provides a default class loader for the agent.
 * <p>
 *
 * @author aland
 * @version 1.0
3 */
public class AgentClassLoader extends ClassLoader {
    // Static block to register the class loader as parallel capable
    static {
        tryRegisterAsParallelCapable();
    }

    private static final Logger logger = LoggerFactory.getLogger(AgentClassLoader.class);

    private List<File> classpath;
    private List<Jar> allJars;
    private final ReentrantLock jarScanLock = new ReentrantLock();

    /**
     * Functional Description: solve the classloader deadlock when jvm start
     * only support JDK7+, since ParallelCapable appears in JDK7+
     */
    private static void tryRegisterAsParallelCapable() {
        Method[] methods = ClassLoader.class.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if ("registerAsParallelCapable".equalsIgnoreCase(methodName)) {
                try {
                    method.setAccessible(true);
                    method.invoke(null);
                } catch (Exception e) {
                    logger.warn("can not invoke ClassLoader.registerAsParallelCapable()", e);
                }
                return;
            }
        }
    }

    /**
     * Returns the default instance of the AgentClassLoader.
     * The default instance is created with the class loader of the ByteKitPlugin class as the parent.
     * @return The default instance of AgentClassLoader
     */
    public static AgentClassLoader getDefault() {
        return DefaultLoaderHolder.DEFAULT_LOADER;
    }

    /**
     * Holder class for the default instance of AgentClassLoader.
     * The default instance is created lazily when getDefault() method is called.
     */
    private static final class DefaultLoaderHolder {
        /**
         * The default class loader for the agent.
         */
        static final AgentClassLoader DEFAULT_LOADER = new AgentClassLoader(ByteKitPlugin.class.getClassLoader());
    }

    /**
     * Constructs an AgentClassLoader with the given parent class loader.
     * The classpath is initialized by scanning the "plugins" directory in the agent home.
     * @param parent The parent class loader
     * @throws BytekitException if the agent home directory or plugins directory cannot be found
     */
    public AgentClassLoader(ClassLoader parent) throws BytekitException {
        super(parent);
        String agentHome = System.getProperty("jagent.home");
        File agentDictionary = new File(agentHome);
        classpath = new LinkedList<>();
        File plugins = new File(agentDictionary, "plugins");
        if (plugins.isDirectory()) {
            File[] files = plugins.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    classpath.add(file);
                }
            }
        }
    }

    /**
     * Finds and loads the class with the specified name.
     * The method searches for the class in all the jars in the classpath.
     * It returns the loaded class if found, otherwise throws ClassNotFoundException.
     * @param name The name of the class to be loaded
     * @return The loaded class
     * @throws ClassNotFoundException if the class cannot be found
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        String path = name.replace('.', '/').concat(".class");
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(path);
            if (entry != null) {
                try {
                    URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
                    byte[] data = getBytes(classFileUrl);
                    return defineClass(name, data, 0, data.length);
                } catch (IOException e) {
                    logger.error("find class fail.", e);
                }
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    /**
     * Reads the bytecode of a class from the given URL.
     * @param classFileUrl The URL of the class file
     * @return The bytecode of the class
     * @throws IOException if an I/O error occurs while reading the class file
     */
    private static byte[] getBytes(URL classFileUrl) throws IOException {
        byte[] data = null;
        BufferedInputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new BufferedInputStream(classFileUrl.openStream());
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            if (baos != null)
                try {
                    baos.close();
                } catch (IOException ignored) {
                }
        }
        return data;
    }

    /**
     * Finds the resource with the given name.
     * The method searches for the resource in all the jars in the classpath.
     * It returns the URL of the resource if found, otherwise returns null.
     * @param name The name of the resource
     * @return The URL of the resource, or null if not found
     */
    @Override
    protected URL findResource(String name) {
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                try {
                    return new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name);
                } catch (MalformedURLException e) {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * Finds all the resources with the given name.
     * The method searches for the resources in all the jars in the classpath.
     * It returns an enumeration of URLs of the resources.
     * @param name The name of the resources
     * @return An enumeration of URLs of the resources
     * @throws IOException if an I/O error occurs while finding the resources
     */
    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<URL>();
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
            }
        }

        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * Scans the classpath and returns a list of all the jars.
     * The method scans each directory in the classpath and adds all the jar files to the list.
     * It uses a lock to ensure thread-safety during scanning.
     * @return A list of all the jars in the classpath
     */
    private List<Jar> getAllJars() {
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = new LinkedList<Jar>();
                    for (File path : classpath) {
                        if (path.exists() && path.isDirectory()) {
                            String[] jarFileNames = path.list((dir, name) -> name.endsWith(".jar"));
                            if (jarFileNames == null) {
                                continue;
                            }
                            for (String fileName : jarFileNames) {
                                try {
                                    File file = new File(path, fileName);
                                    Jar jar = new Jar(new JarFile(file), file);
                                    allJars.add(jar);
                                    logger.debug("{} loaded.", file.toString());
                                } catch (IOException e) {
                                    logger.error("{} jar file can't be resolved", fileName);
                                }
                            }
                        }
                    }
                }
            } finally {
                jarScanLock.unlock();
            }
        }

        return allJars;
    }

    /**
     * Inner class representing a Jar file.
     * It holds a reference to the JarFile object and the corresponding source file.
     */
    private static class Jar {
        private final JarFile jarFile;
        private final File sourceFile;

        private Jar(JarFile jarFile, File sourceFile) {
            this.jarFile = jarFile;
            this.sourceFile = sourceFile;
        }
    }
}
