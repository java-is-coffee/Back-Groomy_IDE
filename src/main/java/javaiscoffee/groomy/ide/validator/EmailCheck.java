package javaiscoffee.groomy.ide.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailCheck {
    String message() default "이메일 형식이 틀렸습니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
