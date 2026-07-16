package com.example.sharedlib.exception;

/**
 * Base class for all custom exceptions in the application.
 * This exception is a {@link RuntimeException} and includes an error code
 * that can be used for internationalization or specific client-side handling.
 */
public class BaseException extends RuntimeException {
    private final String code;

    /**
     * Constructs a new BaseException with the specified error code and detail message.
     *
     * @param code    The error code associated with this exception.
     * @param message The detail message.
     */
    public BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructs a new BaseException with the specified error code, detail message, and cause.
     *
     * @param code    The error code associated with this exception.
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return The error code.
     */

    public String getCode() {
        return code;
    }
}