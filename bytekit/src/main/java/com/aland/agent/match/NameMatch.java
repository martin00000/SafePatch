package com.aland.agent.match;

/**
 * NameMatch
 * <p>
 * Represents a class that matches a given name.
 * </p>
 *
 * @author aland
 * @version 1.0
 * @since 2024/1/16
 */
public class NameMatch implements ClassMatch {
    private String className;

    /**
     * Constructs a new NameMatch object with the specified class name.
     *
     * @param className the name of the class to match
     */
    private NameMatch(String className) {
        this.className = className;
    }

    /**
     * Gets the name of the class being matched.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Creates a new NameMatch object with the specified class name.
     *
     * @param className the name of the class to match
     * @return a new NameMatch object
     */
    public static NameMatch byName(String className) {
        return new NameMatch(className);
    }
}

