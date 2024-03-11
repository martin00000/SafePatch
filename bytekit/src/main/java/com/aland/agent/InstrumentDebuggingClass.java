package com.aland.agent;


import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import com.aland.agent.properties.AgentConfig;
import net.bytebuddy.dynamic.DynamicType;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * The manipulated class output. Write the dynamic classes to the `debugging` folder, when we need to do some debug and
 * recheck.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/16
 *
 */
public enum InstrumentDebuggingClass {
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(InstrumentDebuggingClass.class);
    private File debuggingClassesRootPath;

    public void log(DynamicType dynamicType) {
        if (!AgentConfig.Agent.IS_OPEN_DEBUGGING_CLASS) {
            return;
        }

        /*
          try to do I/O things in synchronized way, to avoid unexpected situations.
         */
        synchronized (INSTANCE) {
            try {
                if (debuggingClassesRootPath == null) {
                    try {
                        debuggingClassesRootPath = new File(AgentConfig.AGENT_HOME, "/debugging/" + AgentConfig.Agent.AGENT_APPLICATION_NAME );
                        if (!debuggingClassesRootPath.exists()) {
                            debuggingClassesRootPath.mkdir();
                        }
                    } catch (Exception e) {
                        logger.error( "Can't find the root path for creating /debugging folder.",e);
                    }
                }

                try {
                    dynamicType.saveIn(debuggingClassesRootPath);
                } catch (IOException e) {
                    logger.error("Can't save class {} to file." , dynamicType.getTypeDescription().getActualName(),e);
                }
            } catch (Throwable t) {
                logger.error("Save debugging classes fail.",t);
            }
        }
    }
}
