package com.example.sharedlib.exception;

/**
 * Exception thrown when a user is authenticated but does not have the necessary permissions
 * to access a resource.
 * Corresponds to an HTTP 403 Forbidden status.
 */
public class ForbiddenException extends BaseException {

    private static final String ERROR_CODE = "FORBIDDEN";

    /**
     * Constructs a new ForbiddenException with the specified detail message.
     *
     * @param message The detail message.
     */
    public ForbiddenException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * Constructs a new ForbiddenException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public ForbiddenException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}