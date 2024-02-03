package javaiscoffee.groomy.ide.member;

import lombok.Data;

@Data
public class PasswordResetDto {
    private Data data;
    @lombok.Data
    public static class Data {
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
