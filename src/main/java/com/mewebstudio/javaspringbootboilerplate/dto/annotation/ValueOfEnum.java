package com.mewebstudio.javaspringbootboilerplate.dto.annotation;

import com.mewebstudio.javaspringbootboilerplate.dto.validator.ValueOfEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();

    String message() default "Invalid value!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
