package com.mewebstudio.javaspringbootboilerplate.dto.validator;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Slf4j
public final class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField;

    private String secondField;

    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstField = constraintAnnotation.first();
        secondField = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        boolean valid = true;

        try {
            final Object firstProperty = BeanUtils.getProperty(obj, firstField);
            final Object secondProperty = BeanUtils.getProperty(obj, secondField);

            valid = (firstProperty == null && secondProperty == null)
                || (firstProperty != null && firstProperty.equals(secondProperty));
        } catch (final Exception e) {
            log.warn("Error while validating fields {} - {}", this.getClass().getName(), e.getMessage());
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(firstField)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
