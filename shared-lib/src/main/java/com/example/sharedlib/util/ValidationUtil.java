package com.example.sharedlib.util;

import com.example.sharedlib.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 * Provides static methods for validating emails, password strength, and other common checks.
 */
public final class ValidationUtil {

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@(.+)$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates the format of an email address.
     *
     * @param email The email address to validate.
     * @throws ValidationException if the email format is invalid.
     */
    public static void validateEmail(String email) {
        if (StringUtils.isBlank(email) || !emailPattern.matcher(email).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    /**
     * Validates the strength of a password based on a set of rules:
     * - Not empty or null
     * - At least 8 characters long
     * - Contains at least one uppercase letter
     * - Contains at least one lowercase letter
     * - Contains at least one digit
     *
     * @param password The password to validate.
     * @throws ValidationException if the password does not meet the strength requirements.
     */
    public static void validatePasswordStrength(String password) {
        if (StringUtils.isBlank(password)) {
            throw new ValidationException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one digit");
        }
    }

    /**
     * Validates that a string value is not null or empty (whitespace is considered empty).
     *
     * @param value     The string value to validate.
     * @param fieldName The name of the field being validated, used in the exception message.
     * @throws ValidationException if the value is null or empty.
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    /**
     * Validates that an object is not null.
     *
     * @param value     The object to validate.
     * @param fieldName The name of the field being validated, used in the exception message.
     * @throws ValidationException if the value is null.
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }
}