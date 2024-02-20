package javaiscoffee.groomy.ide.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordCheck, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }

    private static boolean checkLength(String password) {
        return password == null || password.length() < 8 || password.length() > 20;
    }
}
