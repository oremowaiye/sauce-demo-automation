package com.ore.saucedemo.exceptions;

/**
 * Unchecked exception for framework-level failures (bad configuration, unsupported
 * browser, driver start-up problems) so they are never confused with test assertion
 * failures in the report.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
