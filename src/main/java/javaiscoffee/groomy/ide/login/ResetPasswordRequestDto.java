package javaiscoffee.groomy.ide.login;

import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private EmailCheckDto.Data data;
    @lombok.Data
    public static class Data {
        private String name;
        private String email;
    }
}
