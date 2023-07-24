package com.mewebstudio.javaspringbootboilerplate.dto.validator;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.MinListSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public final class MinListSizeValidator implements ConstraintValidator<MinListSize, List<String>> {
    private long min;

    @Override
    public void initialize(final MinListSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(final List<String> values, final ConstraintValidatorContext context) {
        if (values == null) {
            return true;
        }

        return values.size() >= min;
    }
}
