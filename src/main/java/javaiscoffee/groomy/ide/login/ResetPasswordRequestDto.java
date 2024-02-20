package javaiscoffee.groomy.ide.login;

import javaiscoffee.groomy.ide.validator.EmailCheck;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private String name;
    @EmailCheck
    private String email;
}
