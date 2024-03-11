package com.aland.agent.plugin;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * <p>
 * The PluginContext interface defines the context information and operations for a plugin.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/29
 **/
public interface PluginContext {

     /**
      * Returns the plugin.
      *
      * @return the plugin
      */
     Plugin getPlugin();

     /**
      * Retrieves the value of the property associated with the specified key.
      *
      * @param  key  the key of the property to retrieve
      * @return      the value of the property
      */
     String getProperty(String key);

     /**
      * Retrieves the current instrumentation instance.
      *
      * @return the current instrumentation instance
      */
     Instrumentation getInstrumentation();

     /**
      * Converts the specified Java class to an array of bytes.
      *
      * @param  theClass  the Java class to be converted
      * @return           an array of bytes representing the specified class
      * @throws IOException if an I/O error occurs while converting the class to bytes
      */
     byte[] toBytes(Class<?> theClass) throws IOException;

     /**
      * Redefines the specified class with the provided class file.
      *
      * @param  theClass      the class to be redefined
      * @param  theClassFile  the byte array representing the new class file
      * @throws ClassNotFoundException      if the class cannot be found
      * @throws UnmodifiableClassException  if the class cannot be modified
      */
     void redefine(Class<?> theClass, byte[] theClassFile) throws ClassNotFoundException, UnmodifiableClassException;

}
