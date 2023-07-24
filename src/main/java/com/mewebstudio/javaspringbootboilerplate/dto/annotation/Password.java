package com.mewebstudio.javaspringbootboilerplate.dto.annotation;

import com.mewebstudio.javaspringbootboilerplate.dto.validator.PasswordConstraintsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordConstraintsValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Password {
    String message() default "Invalid password.";

    boolean detailedMessage() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
