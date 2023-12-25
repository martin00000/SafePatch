package com.aland;

import com.aland.match.ClassMatch;
import net.bytebuddy.dynamic.DynamicType;

public interface ByteKitPlugin {


    /**
     * 转换后的类
     * @param transformClassName
     * @param builder
     * @param classLoader
     * @param enhanceContext
     * @return
     * @throws BytekitException
     */
    DynamicType.Builder<?> define(String transformClassName,
                                  DynamicType.Builder<?> builder, ClassLoader classLoader, EnhanceContext enhanceContext) throws BytekitException;


    /**
     * AOP匹配入口
     * @return
     */
    ClassMatch enhanceClass();
}
