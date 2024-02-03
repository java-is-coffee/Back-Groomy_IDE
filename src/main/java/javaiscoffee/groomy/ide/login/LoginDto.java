package javaiscoffee.groomy.ide.login;

import lombok.Data;

@Data
public class LoginDto {

    private Data data;
    @lombok.Data
    public static class Data {
        private String email;
        private String password;
    }
}
