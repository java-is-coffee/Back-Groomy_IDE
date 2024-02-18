package javaiscoffee.groomy.ide.oauth.service;

import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.groomy.ide.oauth.GoogleOAuth;
import javaiscoffee.groomy.ide.oauth.SocialOAuth;
import javaiscoffee.groomy.ide.oauth.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {
    private final SocialOAuth socialOAuth;
    private final HttpServletResponse response;
    private final GoogleOAuth googleOAuth;

    public void request(SocialType socialType) {
        SocialOAuth socialOauth = this.findSocialOauthByType(socialType);
        String redirectURL = googleOAuth.getOauthRedirectURL();
        try {
            log.info("redirect 처리 할 URL 생성 후 sendRedirect 처리");
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String requestAccessToken(SocialType socialType, String code) {
        SocialOAuth socialOauth = this.findSocialOauthByType(socialType);
        return socialOauth.requestAccessToken(code);
    }

    private SocialOAuth findSocialOauthByType(SocialType socialType) {
        if (socialOAuth.type() == socialType) {
            return socialOAuth;
        } else {
            throw new IllegalArgumentException("알 수 없는 SocialLoginType 입니다.");
        }
    }
}
