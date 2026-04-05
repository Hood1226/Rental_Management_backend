package com.rental.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters and include an uppercase letter, a number and a special character (!@#$%^&*(),.?\":{}|<>_-+=)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
