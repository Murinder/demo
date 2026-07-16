package com.example.sharedlib.exception;

/**
 * Exception thrown when a specific resource is not found in the system.
 * Corresponds to an HTTP 404 Not Found status.
 */
public class ResourceNotFoundException extends BaseException {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    /**
     * Constructs a new ResourceNotFoundException with a formatted message.
     *
     * @param resourceName The name of the resource that was not found (e.g., "User", "Project").
     * @param fieldName    The name of the field used for the lookup (e.g., "id", "email").
     * @param fieldValue   The value of the field used for the lookup.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ERROR_CODE, String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom message.
     *
     * @param message The detail message.
     */
    public ResourceNotFoundException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}