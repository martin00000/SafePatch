/**
 * <p>
 * BytekitException is a subclass of RuntimeException used to represent exceptions in the Bytekit library.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/23
 */

package com.aland.agent;


public class BytekitException extends RuntimeException {
    private static final long serialVersionUID = -6020188711867490724L;

    public BytekitException(String message) {
        super(message);
    }

    public BytekitException(String message, Throwable cause) {
        super(message, cause);
    }
}
