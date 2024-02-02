package Javaiscoffee.Groomy.IDE.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
