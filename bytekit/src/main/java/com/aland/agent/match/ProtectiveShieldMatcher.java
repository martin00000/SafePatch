
package com.aland.agent.match;

import com.aland.agent.logger.Logger;
import com.aland.agent.logger.LoggerFactory;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * ProtectiveShieldMatcher
 * <p>
 * This class is a generic implementation of the ElementMatcher.Junction.AbstractBase class.
 * It is used to match a target object against a given matcher.
 * If an exception occurs during the matching process, a warning message is logged and false is returned.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/16
 */
public class ProtectiveShieldMatcher<T> extends ElementMatcher.Junction.AbstractBase<T> {
    private Logger logger = LoggerFactory.getLogger(ProtectiveShieldMatcher.class);
    private final ElementMatcher<? super T> matcher;

    /**
     * Constructs a new ProtectiveShieldMatcher with the given matcher.
     *
     * @param matcher the matcher to be used for matching the target object
     */
    public ProtectiveShieldMatcher(ElementMatcher<? super T> matcher) {
        this.matcher = matcher;
    }

    /**
     * Matches the target object against the given matcher.
     *
     * @param target the target object to be matched
     * @return true if the target object matches the given matcher, false otherwise
     */
    public boolean matches(T target) {
        try {
            return this.matcher.matches(target);
        } catch (Throwable t) {
            logger.warn("Byte-buddy occurs exception when match type.", t);
            return false;
        }
    }
}
