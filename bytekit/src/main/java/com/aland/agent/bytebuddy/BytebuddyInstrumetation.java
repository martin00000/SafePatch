package com.aland.agent.bytebuddy;

import com.aland.agent.ByteKitPlugin;
import com.aland.agent.EnhanceContext;
import com.aland.agent.InstrumentDebuggingClass;
import com.aland.agent.loader.AgentClassLoader;
import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.match.ClassMatch;
import com.aland.agent.match.IndirectMatch;
import com.aland.agent.match.NameMatch;
import com.aland.agent.match.ProtectiveShieldMatcher;
import com.aland.agent.plugin.PluginContext;
import com.aland.agent.properties.AgentConfig;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * BytebuddyInstrumetation
 * <p>
 * This code defines a class called BytebuddyInstrumetation that is used for bytecode instrumentation in a Java application.
 * It provides methods to add ByteKit plugins for instrumentation and start the instrumentation process.
 * The class also includes a Transformer inner class that implements the AgentBuilder.Transformer interface.
 * The transform() method of the Transformer class is called during the class transformation process and modifies the class builder based on the plugins.
 * The class also includes a Listener inner class that implements the AgentBuilder.Listener interface and handles various events during the class transformation process.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/15
 */
public class BytebuddyInstrumetation {

    private static final Logger logger = LoggerFactory.getLogger(BytebuddyInstrumetation.class);

    private final Map<String, LinkedList<ByteKitPlugin>> nameMatchDefine = new HashMap<>();

    private final List<ByteKitPlugin> signatureMatchDefine = new LinkedList<>();

    private final Map<ByteKitPlugin, PluginContext> byteKitPluginContextMap = new ConcurrentHashMap<>();

    private static Map<ClassLoader, Map<String, List<ByteKitPlugin>>> cacheByteKitPlugin = new HashMap<>();


    public void install(Instrumentation instrumentation) {

    }

    /**
     * Adds Bytekit plugin instrumentation to the BytebuddyInstrumetation object.
     *
     * @param byteKitPluginName  the name of the Bytekit plugin to be added
     * @param pluginContext      the plugin context object
     * @return                   the modified BytebuddyInstrumetation object
     */
    public BytebuddyInstrumetation addBytekitPluginInstrumentation(String byteKitPluginName, PluginContext pluginContext) {
        try {
            addInstrumentation((ByteKitPlugin) Class.forName(byteKitPluginName, true, AgentClassLoader.getDefault()).newInstance(), pluginContext);
        } catch (Exception e) {
            logger.warn("addBytekitPluginInstrumentation " + byteKitPluginName, e);
        }

        return this;
    }

    /**
     * Adds Bytekit plugin instrumentation to the BytebuddyInstrumetation.
     *
     * @param  byteKitPlugin  the ByteKitPlugin to add
     * @param  pluginContext  the PluginContext to use
     * @return                the modified BytebuddyInstrumetation
     */
    public BytebuddyInstrumetation addBytekitPluginInstrumentation(ByteKitPlugin byteKitPlugin, PluginContext pluginContext) {
        addInstrumentation(byteKitPlugin, pluginContext);
        return this;
    }

    /**
     * Adds instrumentation to a Java class using ByteKit.
     *
     * @param  byteKitPlugin   the ByteKitPlugin used to add instrumentation
     * @param  pluginContext   the PluginContext providing the context for the plugin
     */
    private void addInstrumentation(ByteKitPlugin byteKitPlugin, PluginContext pluginContext) {
        ClassMatch classMatch = byteKitPlugin.enhanceClass();
        if (classMatch == null) {
            return;
        }
        byteKitPluginContextMap.put(byteKitPlugin, pluginContext);
        if (classMatch instanceof NameMatch) {
            NameMatch nameMatch = (NameMatch) classMatch;
            LinkedList<ByteKitPlugin> byteKitPlugins = nameMatchDefine.computeIfAbsent(nameMatch.getClassName(), k -> new LinkedList<>());
            byteKitPlugins.add(byteKitPlugin);
        } else {
            signatureMatchDefine.add(byteKitPlugin);
        }
    }

    /**
     * Starts the instrumentation process.
     *
     * @param  instrumentation   the instrumentation object
     * @param  context           the plugin context
     */
    public void startInstrumentation(Instrumentation instrumentation, PluginContext context) {
        AgentBuilder.LocationStrategy.ForClassLoader locationStrategy = AgentBuilder.LocationStrategy.ForClassLoader.WEAK;

        ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(AgentConfig.Agent.IS_OPEN_DEBUGGING_CLASS));

        new AgentBuilder.Default(byteBuddy)
                .with(locationStrategy)
                .ignore(nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("org.slf4j."))  //slf4j
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(nameStartsWith("com.intellij.rt.debugger.agent")) //idea调试模式出错，排除
                        .or(nameStartsWith("com.aland.agent"))
                        .or(nameStartsWith("java."))//rt.jar bootstrap classloader
                        .or(nameStartsWith("com.sun."))
                        .or(nameStartsWith("one.profiler."))
                        .or(ElementMatchers.<TypeDescription>isSynthetic()))
                .type(buildMatch())
                .transform(new Transformer())
                .with(new Listener())
                .installOn(instrumentation);
    }

    /**
     * Builds the match for the ElementMatcher.
     *
     * @return  the ElementMatcher for the TypeDescription
     */
    private ElementMatcher<? super TypeDescription> buildMatch() {
        ElementMatcher.Junction junction = new ElementMatcher.Junction.AbstractBase<NamedElement>() {
            @Override
            public boolean matches(NamedElement namedElement) {
                return nameMatchDefine.containsKey(namedElement.getActualName());
            }
        };
        junction = junction.and(not(isInterface()));

        for (ByteKitPlugin byteKitPlugin : signatureMatchDefine) {
            ClassMatch match = byteKitPlugin.enhanceClass();
            if (match instanceof IndirectMatch) {
                junction = junction.or(((IndirectMatch) match).buildJunction());
            }
        }
        return new ProtectiveShieldMatcher<>(junction);
    }


    /**
     * This code defines a private class called Transformer that implements the AgentBuilder.Transformer interface.
     * The transform() method of the Transformer class takes a DynamicType.Builder, TypeDescription, ClassLoader, and JavaModule as parameters.
     * It iterates over a list of ByteKitPlugins and performs various operations on the builder based on the plugins.
     * The method returns the modified builder.
     */
    private class Transformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
            DynamicType.Builder<?> newBuilder = builder;

            for (ByteKitPlugin byteKitPlugin : getMatchDefine(typeDescription)) {
                EnhanceContext enhanceContext = new EnhanceContext();

                Map<String, List<ByteKitPlugin>> classLoaderMapMap = cacheByteKitPlugin.get(classLoader);
                if (classLoaderMapMap == null) {
                    synchronized (BytebuddyInstrumetation.class) {
                        classLoaderMapMap = cacheByteKitPlugin.computeIfAbsent(classLoader, k -> new ConcurrentHashMap<>());
                    }
                }

                List<ByteKitPlugin> byteKitPlugins = classLoaderMapMap.get(typeDescription.getName());
                if (byteKitPlugins != null) {
                    logger.info("name:{} ,classloader:{},exists ", typeDescription.getName(), classLoader);
                    byteKitPlugins.add(byteKitPlugin);
                    enhanceContext.extendObjectCompleted();
                }

                enhanceContext.setPluginContext(byteKitPluginContextMap.get(byteKitPlugin));

                DynamicType.Builder<?> possibleNewBuilder = byteKitPlugin.define(typeDescription.getName(), builder, classLoader, enhanceContext);
                if (possibleNewBuilder != null) {
                    newBuilder = possibleNewBuilder;
                    if (byteKitPlugins == null) {
                        byteKitPlugins = new LinkedList<>();
                        byteKitPlugins.add(byteKitPlugin);
                        classLoaderMapMap.put(typeDescription.getName(), byteKitPlugins);
                    }
                }
                logger.debug("Finish the prepare stage for {}.", typeDescription.getName());
            }
            logger.debug("Matched class {}, but ignore by finding mechanism.", typeDescription.getTypeName());
            return newBuilder;
        }
    }

    /**
     * Retrieves a list of ByteKitPlugin objects that match the given TypeDescription.
     *
     * @param  typeDescription  the TypeDescription to match against
     * @return                  a list of ByteKitPlugin objects that match the given TypeDescription
     */
    private List<ByteKitPlugin> getMatchDefine(TypeDescription typeDescription) {
        List<ByteKitPlugin> matchDefine = new LinkedList<>();
        String typeName = typeDescription.getTypeName();
        LinkedList<ByteKitPlugin> nameMatchDefineList = nameMatchDefine.get(typeName);
        if (nameMatchDefineList != null) {
            matchDefine.addAll(nameMatchDefineList);
        }

        for (ByteKitPlugin byteKitPlugin : signatureMatchDefine) {
            IndirectMatch match = (IndirectMatch) byteKitPlugin.enhanceClass();
            if (match.isMatch(typeDescription)) {
                matchDefine.add(byteKitPlugin);
            }
        }
        return matchDefine;
    }

    private class Listener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {

        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean loaded, DynamicType dynamicType) {
            if (logger.isDebugEnabled()) {
                logger.debug("On Transformation class {}.", typeDescription.getName());
            }
            InstrumentDebuggingClass.INSTANCE.log(dynamicType);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {

        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded, Throwable throwable) {
            logger.error("Enhance class " + typeName + " error.", throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {

        }
    }
}
