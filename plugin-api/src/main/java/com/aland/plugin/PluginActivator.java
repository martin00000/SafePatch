package com.aland.plugin;


/**
 * Class PluginActivator
 * <p>
 * Created by aland on 2023/12/25.
 * 插件激活器
 *
 * @version 1.0
 */
public interface PluginActivator {

    /**
     * 让插件本身判断是否要启动
     */
    boolean enabled(PluginContext context);

    /**
     * 初始化插件
     * @param context
     * @throws Exception
     */
     void init(PluginContext context) throws Exception;

    /**
     * 启动插件
     * @param context
     * @throws Exception
     */
     void start(PluginContext context) throws Exception;

    /**
     * 停止插件
     * @param context
     * @throws Exception
     */
     void stop(PluginContext context) throws Exception;
}

