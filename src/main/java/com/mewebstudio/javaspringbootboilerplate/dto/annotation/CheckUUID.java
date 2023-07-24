package com.mewebstudio.javaspringbootboilerplate.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, ANNOTATION_TYPE})
@Constraint(validatedBy = {})
@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
@Documented
public @interface CheckUUID {
    String message() default "UUID has wrong format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
