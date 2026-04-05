package com.rental.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final String UPPER = ".*[A-Z].*";
    private static final String DIGIT = ".*[0-9].*";
    private static final String SPECIAL = ".*[!@#$%^&*(),.?\":{}|<>_\\-+=].*";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // use @NotBlank for required; here we only validate format when present
        }
        if (value.length() < MIN_LENGTH) {
            return false;
        }
        return value.matches(UPPER) && value.matches(DIGIT) && value.matches(SPECIAL);
    }
}
