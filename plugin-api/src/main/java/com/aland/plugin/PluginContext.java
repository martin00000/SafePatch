package com.aland.plugin;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public interface PluginContext {

     /**
      * 获取插件信息
      * @return
      */
     Plugin getPlugin();

     /**
      * 获取插件属性(例：agent.home=agent启动目录)
      * @param key
      * @return
      */
     String getProperty(String key);

     /**
      * 获取Instrumentation用于字节码增强
      * @return
      */
     Instrumentation getInstrumentation();


     /**
      * 得到类的字节信息
      * @param theClass
      * @return
      */
     byte[] toBytes(Class<?> theClass) throws IOException;

     /**
      * 重新定义类的字节信息
      * @param theClass       要替换的类
      * @param theClassFile   类字节
      */
     void redefine(Class<?> theClass, byte[] theClassFile) throws ClassNotFoundException, UnmodifiableClassException;

}
