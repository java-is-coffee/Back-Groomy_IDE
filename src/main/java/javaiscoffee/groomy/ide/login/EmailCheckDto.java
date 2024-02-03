package javaiscoffee.groomy.ide.login;

import lombok.Data;

@Data
public class EmailCheckDto {
    private Data data;
    @lombok.Data
    public static class Data {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
