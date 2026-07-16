package com.example.sharedlib.validation;

import com.example.sharedlib.validation.validator.EnumValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to check if a string value is a valid enum constant.
 * The validation is case-insensitive.
 * <p>
 * Example usage:
 * <pre>
 *     {@literal @}EnumValidator(enumClass = ProjectStatus.class, message = "Invalid project status")
 *     private String status;
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorImpl.class)
public @interface EnumValidator {

    /**
     * @return The enum class to validate against.
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * @return The error message to be returned if validation fails.
     */
    String message() default "Value is not a valid enum constant";

    /**
     * @return The validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * @return The payload.
     */
    Class<? extends Payload>[] payload() default {};
}