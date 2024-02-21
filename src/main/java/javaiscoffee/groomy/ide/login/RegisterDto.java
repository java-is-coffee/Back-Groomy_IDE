package javaiscoffee.groomy.ide.login;

import lombok.Data;

@Data
public class RegisterDto {
    private Data data;

    @lombok.Data
    public static class Data {
        private String email;
        private String password;
        private String name;
        private String nickname;
        private String certificationNumber;
    }
}

