package javaiscoffee.groomy.ide.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailCheck, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(checkIsEmpty(email)) return false;
        return checkIsEmail(email);
    }

    private static boolean checkIsEmpty(String email) {
        return email == null || email.length() == 0;
    }

    private static boolean checkIsEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
