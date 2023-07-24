package com.mewebstudio.javaspringbootboilerplate.dto.validator;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Arrays;
import java.util.List;

public final class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {
    private static final int MIN_LENGTH = 6;

    private static final int MAX_LENGTH = 32;

    private boolean detailedMessage;

    @Override
    public void initialize(final Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        detailedMessage = constraintAnnotation.detailedMessage();
    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
            // Length rule. Min 6 max 32 characters
            new LengthRule(MIN_LENGTH, MAX_LENGTH),
            // At least one upper case letter
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            // At least one lower case letter
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            // At least one number
            new CharacterRule(EnglishCharacterData.Digit, 1),
            // At least one special characters
            new CharacterRule(EnglishCharacterData.Special, 1),
            // No whitespace
            new WhitespaceRule()
        ));

        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        if (detailedMessage) {
            List<String> messages = validator.getMessages(result);
            String messageTemplate = String.join("\n", messages);
            context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        }

        return false;
    }
}
