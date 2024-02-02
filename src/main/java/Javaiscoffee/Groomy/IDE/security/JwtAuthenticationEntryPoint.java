package Javaiscoffee.Groomy.IDE.security;

import Javaiscoffee.Groomy.IDE.response.MyResponse;
import Javaiscoffee.Groomy.IDE.response.ResponseStatus;
import Javaiscoffee.Groomy.IDE.response.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        MyResponse<Object> myResponse = new MyResponse<>(new Status(ResponseStatus.LOGIN_FAILED));
        String jsonResponse = new ObjectMapper().writeValueAsString(myResponse);

        response.getWriter().write(jsonResponse);
    }
}

