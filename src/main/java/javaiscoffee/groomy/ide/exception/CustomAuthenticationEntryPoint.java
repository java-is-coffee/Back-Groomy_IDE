package javaiscoffee.groomy.ide.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 스프링 시큐리티가 /error로 보냈을 때 리디렉션 시키지 않고
 * 에러 response 보내도록 조정하는 Handler
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // HTTP 상태 코드 401만 반환하고 Google 로그인 페이지로 리다이렉트하지 않음
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "허용되지 않는 접근입니다.");
    }
}

