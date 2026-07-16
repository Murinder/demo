package com.example.securitylib.exception;

/**
 * Exception thrown when an authentication attempt fails due to invalid credentials,
 * expired tokens, or other authentication-related issues.
 * Corresponds to an HTTP 401 Unauthorized status.
 */
public class AuthenticationException extends BaseException {

    private static final String ERROR_CODE = "AUTHENTICATION_ERROR";

    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message The detail message.
     */
    public AuthenticationException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * Constructs a new AuthenticationException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}