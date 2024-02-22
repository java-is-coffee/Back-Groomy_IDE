package javaiscoffee.groomy.ide.login.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.groomy.ide.login.oauth.userInfo.CustomOAuthUser;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import javaiscoffee.groomy.ide.security.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자가 인증되면서 요청이 성공하면 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth 로그인 성공!!");
        try {
            CustomOAuthUser oAuthUser = (CustomOAuthUser) authentication.getPrincipal();
            log.info("OAuth 로그인 email 확인 = {}", oAuthUser.getEmail());
            log.info("authentication.getprincipal() 반환 객체 = {}", authentication.getPrincipal());


            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            log.info("OAuth tokenDto = {}", tokenDto);

            // Bearer + 토큰
            String accessToken = tokenDto.getAccessToken();
            String refreshToken = tokenDto.getRefreshToken();
            response.sendRedirect("http://localhost:3000?access_token="+accessToken+"&refresh_token="+refreshToken);

//            response.sendRedirect("http://localhost:3000?access_token="+accessToken+"&refresh_token="+refreshToken);

            //HTTP 응답에 토큰 정보를 포함하여 클라이언트에게 전송
            response.setContentType("application/json");  //HTTP 응답의 콘텐츠 유형을 JSON 형식으로 설정한다는 것을 의미
            new ObjectMapper().writeValue(response.getWriter(), tokenDto);

        } catch (Exception e) {
            log.info("소셜 로그인 실패 = {}", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

}


