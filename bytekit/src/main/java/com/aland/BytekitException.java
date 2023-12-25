package com.aland;


public class BytekitException extends RuntimeException {
    private static final long serialVersionUID = -6020188711867490724L;

    public BytekitException(String message) {
        super(message);
    }

    public BytekitException(String message, Throwable cause) {
        super(message, cause);
    }
}
