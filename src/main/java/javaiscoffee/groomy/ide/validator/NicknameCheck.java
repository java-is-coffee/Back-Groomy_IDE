package javaiscoffee.groomy.ide.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NicknameValidator.class)
public @interface NicknameCheck {
    String message() default "닉네임이 조건을 만족하지 않습니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
