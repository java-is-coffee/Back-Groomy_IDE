package Javaiscoffee.Groomy.IDE.security;

import Javaiscoffee.Groomy.IDE.response.MyResponse;
import Javaiscoffee.Groomy.IDE.response.ResponseStatus;
import Javaiscoffee.Groomy.IDE.response.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        log.info("로그인 실패해서 JwtEntryPoint 진입");
        MyResponse<Object> myResponse = new MyResponse<>(new Status(ResponseStatus.LOGIN_FAILED));
        String jsonResponse = new ObjectMapper().writeValueAsString(myResponse);

        response.getWriter().write(jsonResponse);
    }
}

