package com.ore.saucedemo.exceptions;

/**
 * Unchecked exception for framework problems: bad configuration, an unsupported
 * browser, a driver that will not start. Keeping them in their own type stops the
 * report confusing them with a failed assertion.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
