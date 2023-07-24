package com.mewebstudio.javaspringbootboilerplate.dto.validator;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.ValueOfEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public final class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize(final ValueOfEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .toList();
    }

    @Override
    public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return acceptedValues.contains(value.toString().toUpperCase());
    }
}
