package javaiscoffee.groomy.ide.login.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.groomy.ide.login.LoginService;
import javaiscoffee.groomy.ide.login.oauth.OAuthAttributes;
import javaiscoffee.groomy.ide.login.oauth.SocialType;
import javaiscoffee.groomy.ide.login.oauth.userInfo.CustomOAuthUser;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.member.MemberRepository;
import javaiscoffee.groomy.ide.member.MemberRole;
import javaiscoffee.groomy.ide.member.MemberStatus;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import javaiscoffee.groomy.ide.security.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final LoginService loginService;

    @Autowired
    public OAuthLoginSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository, @Lazy LoginService loginService) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginService = loginService;
    }

    // 사용자가 인증되면서 요청이 성공하면 호출되는 메서드
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth 로그인 성공!!");
        try {
            // 주체 추출
            Object principal = authentication.getPrincipal();
            // 토큰 생성에 필요한 변수 초기화
            String email = null;
            Long memberId = null;
            Collection<? extends GrantedAuthority> authorities = null;

            if (principal instanceof CustomOAuthUser) {
                CustomOAuthUser customOAuthUser = (CustomOAuthUser) principal;
                email = customOAuthUser.getEmail();
                memberId = customOAuthUser.getMemberId();
                authorities = customOAuthUser.getAuthorities();
                log.info("email = {}, memberId = {}", email, memberId);
            } else if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                email = oAuth2User.getAttribute("email");
                // DB에서 사용자 조회 또는 새로 생성
                Member member = memberRepository.findByEmail(email)
                        .orElseGet(() -> {
                            // OAuth2User에서 OAuthAttributes를 생성하는 로직 필요
                            // 아래의 toEntity 메서드는 예시일 뿐, OAuth2User의 정보를 바탕으로
                            // 적절한 Member 객체를 생성하는 메서드를 구현해야 함
                            OAuthAttributes attributes = OAuthAttributes.of(SocialType.GOOGLE,"sub",oAuth2User.getAttributes());
                            return loginService.saveUser(attributes, SocialType.GOOGLE); // 적절한 SocialType 지정 필요
                        });
                if(member.getStatus() == MemberStatus.DELETED) {
                    throw new BaseException(ResponseStatus.BAD_REQUEST.getMessage());
                }
                log.info("찾은 로그인 멤버 = {}",member);
                memberId = member.getMemberId();
            }

            log.info("authentication.getprincipal() 반환 객체 = {}", authentication.getPrincipal());
            log.info("토큰 만들기 직전 memberId = {}",memberId);


            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication,memberId,email);
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


