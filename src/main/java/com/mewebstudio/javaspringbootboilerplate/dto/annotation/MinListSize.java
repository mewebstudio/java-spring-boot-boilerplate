package com.mewebstudio.javaspringbootboilerplate.dto.annotation;

import com.mewebstudio.javaspringbootboilerplate.dto.validator.MinListSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MinListSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinListSize {
    String message() default "The list must be a minimum size";

    long min() default -1L;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
