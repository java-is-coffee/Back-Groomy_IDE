package javaiscoffee.groomy.ide.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<PasswordCheck, String> {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    /**
     * 비밀번호 검사
     * 8글자 이상 20글자 이하만 가능
     * 대문자,소문자,특수문자 1개씩 포함해야 함
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return checkLength(password) && checkCharacter(password);
    }

    private static boolean checkLength(String password) {
        return password.length() >= 8 && password.length() <= 20;
    }

    private static boolean checkCharacter(String password) {
        return UPPERCASE_PATTERN.matcher(password).find() &&
                LOWERCASE_PATTERN.matcher(password).find() &&
                SPECIAL_CHAR_PATTERN.matcher(password).find();
    }
}
