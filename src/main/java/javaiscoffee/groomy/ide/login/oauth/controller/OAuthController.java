package javaiscoffee.groomy.ide.login.oauth.controller;

import javaiscoffee.groomy.ide.login.oauth.SocialType;
import javaiscoffee.groomy.ide.login.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping
public class OAuthController {
    private final OAuthService oAuthService;

    /**
     * '구글로 로그인' 버튼 클릭 시 실행
     * 사용자로부터 소셜 로그인 요청을 SocialType을 받아서 처리
     * @param socialType
     */
    @GetMapping("/auth/{socialType}")
    public void socialLoginType(@PathVariable(name = "socialType") SocialType socialType) {
        log.info(">> 사용자로부터 소셜 로그인 요청을 받음 :: {} Social Login", socialType);
        oAuthService.request(socialType);
    }

    /**
     * 사용자가 로그인 시도한 후, 인증에 성공 했을 때 리디렉션
     * URL 매개변수에 인증코드(authorization code)가 담겨서 오는데 그 때 수행
     * @param socialType
     * @param code
     * @return
     */
    @GetMapping("/login/oauth2/{socialType}")
    public String callback(@PathVariable(name = "socialType") SocialType socialType, @RequestParam(name = "code") String code) {
        // OAuth 콜백에서 전달된 인증 코드를 사용하여 엑세스 토큰 요청
        // 엑세스 토큰 요청 및 사용 후 응답 처리
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);
        return oAuthService.requestAccessToken(socialType, code);
    }
}
