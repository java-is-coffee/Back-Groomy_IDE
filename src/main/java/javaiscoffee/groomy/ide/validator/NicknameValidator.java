package javaiscoffee.groomy.ide.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 이메일 검사
 * 이메일 형태에 맞아야 함
 */

public class NicknameValidator implements ConstraintValidator<NicknameCheck, String> {

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        if(nickname == null) return false;
        return checkLength(nickname);
    }

    private static boolean checkLength(String nickname) {
        return nickname.length() >= 2 && nickname.length() <= 20;
    }

}
