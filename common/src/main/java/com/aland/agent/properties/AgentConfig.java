package com.aland.agent.properties;

/**
 * AgentConfig
 * <p>
 * defines the agent configuration information
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/16
 */
public class AgentConfig {

    public static String AGENT_HOME = null;

    public static class Agent {

        /**
         * If true, jagent agent will save all instrumented classes files in `/debugging` folder.
         */
        public static boolean IS_OPEN_DEBUGGING_CLASS = false;

        /**
         * application name
         */
        public static String AGENT_APPLICATION_NAME = "default";

    }

}
