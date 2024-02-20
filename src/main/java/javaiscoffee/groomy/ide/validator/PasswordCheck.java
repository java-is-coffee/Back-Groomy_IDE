package javaiscoffee.groomy.ide.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordCheck {
    String message() default "비밀번호 형식이 틀렸습니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
