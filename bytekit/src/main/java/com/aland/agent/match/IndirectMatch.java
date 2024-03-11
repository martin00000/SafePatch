package com.aland.agent.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * IndirectMatch
 * <p>
 * IndirectMatch is an interface that extends ClassMatch. It provides methods to build a junction and check if a given type description is a match.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/16
 */
public interface IndirectMatch extends ClassMatch {
    /**
     * Builds a junction for indirect matching.
     *
     * @return The built junction.
     */
    ElementMatcher.Junction buildJunction();

    /**
     * Checks if the given type description is a match.
     *
     * @param typeDescription The type description to check.
     * @return True if the type description is a match, false otherwise.
     */
    boolean isMatch(TypeDescription typeDescription);
}
