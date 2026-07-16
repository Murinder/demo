package com.example.sharedlib.validation.validator;

import com.example.sharedlib.validation.EnumValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the {@link EnumValidator} annotation.
 * This validator checks if a given string is a valid constant of the specified enum class.
 * The comparison is case-insensitive.
 */
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        allowedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // or false, depending on whether null values are allowed
        }
        return allowedValues.stream().anyMatch(value::equalsIgnoreCase);
    }
}