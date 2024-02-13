package javaiscoffee.groomy.ide.security;

import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter
 * 클라이언트 요청 시 JWT 인증을 하기 위해 설치하는 커스텀 필터로
 * UsernamePasswordAuthenticationFilter 이전에 실행된다.
 * 이전에 실행된다는 뜻은 JwtAuthenticationFilter를 통과하면
 * UsernamePasswordAuthenticationFilter 이후의 필터는 통과한 것으로 본다는 뜻이다.
 * 쉽게 말해서, Username + Password를 통한 인증을 Jwt를 통해 수행한다는 것이다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 로그인과 회원가입 요청에 대해서는 필터를 적용하지 않음
        if ("/api/member/login".equals(requestURI) || "/api/member/register".equals(requestURI)
                || "/api/member/refresh".equals(requestURI) || "/api/member/register/email-check".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } else {
            log.info("토큰 에러 = {}",token);
            //access token이 잘못되어서 검사 실패했을 경우
            Status status = new Status(ResponseStatus.UNAUTHORIZED);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            // ObjectMapper를 사용하여 MyResponse 객체를 JSON으로 변환
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(status);

            response.getWriter().write(jsonResponse);
        }
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
