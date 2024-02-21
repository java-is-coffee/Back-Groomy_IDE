package javaiscoffee.groomy.ide.login;

import javaiscoffee.groomy.ide.validator.EmailCheck;
import javaiscoffee.groomy.ide.validator.NicknameCheck;
import javaiscoffee.groomy.ide.validator.PasswordCheck;
import lombok.Data;

@Data
public class RegisterDto {
    @EmailCheck
    private String email;
    @PasswordCheck
    private String password;
    private String name;
    @NicknameCheck
    private String nickname;
    private String certificationNumber;
}

