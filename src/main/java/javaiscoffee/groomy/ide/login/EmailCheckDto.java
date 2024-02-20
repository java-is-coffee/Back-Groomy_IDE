package javaiscoffee.groomy.ide.login;

import jakarta.validation.Valid;
import javaiscoffee.groomy.ide.validator.EmailCheck;
import lombok.Data;

@Data
public class EmailCheckDto {
    @EmailCheck
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
