package com.aland.plugin;


import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Properties;

/**
 * Class  PluginContextImpl
 * <p>
 * Created by aland on 2023/12/25.
 *
 * @version 1.0
 */
public class PluginContextImpl implements PluginContext {
    private Plugin plugin;

    private Properties properties;
    private Instrumentation instrumentation;

//    private List<ByteKitPlugin> byteKitPlugins = new ArrayList<>();

    public PluginContextImpl(Plugin plugin, Instrumentation instrumentation, Properties properties) {
        this.plugin = plugin;
        this.instrumentation = instrumentation;
        this.properties = properties;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

//    @Override
//    public void addInstrumentation(ByteKitPlugin byteKitPlugin) {
//        byteKitPlugins.add(byteKitPlugin);
//    }

    @Override
    public byte[] toBytes(Class<?> theClass) throws IOException {
        String resource = theClass.getName().replace('.', '/') + ".class";
        InputStream is = theClass.getClassLoader().getResourceAsStream(resource);
        return IOUtils.toByteArray(is);
    }

    @Override
    public void redefine(Class<?> theClass, byte[] theClassFile) throws ClassNotFoundException, UnmodifiableClassException {
        ClassDefinition classDefinition = new ClassDefinition(theClass, theClassFile);
        getInstrumentation().redefineClasses(classDefinition);
    }

    @Override
    public <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        return ExtensionLoader.getExtensionLoader(type,this);
    }

//    @Override
//    public void redefine(String classDescribe, byte[] theClassFile)throws ClassNotFoundException, UnmodifiableClassException{
//        throw new NotImplementedException();
//    }



}
