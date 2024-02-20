package javaiscoffee.groomy.ide.login;

import javaiscoffee.groomy.ide.validator.EmailCheck;
import javaiscoffee.groomy.ide.validator.PasswordCheck;
import lombok.Data;

@Data
public class LoginDto {
    @EmailCheck
    private String email;
    @PasswordCheck
    private String password;
}
