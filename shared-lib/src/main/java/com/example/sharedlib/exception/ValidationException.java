package com.example.sharedlib.exception;

/**
 * Exception thrown when input data fails validation checks.
 * This can be due to invalid formats, missing fields, or other data integrity issues.
 * Corresponds to an HTTP 400 Bad Request status.
 */
public class ValidationException extends BaseException {

    private static final String ERROR_CODE = "VALIDATION_ERROR";

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message The detail message.
     */
    public ValidationException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public ValidationException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}