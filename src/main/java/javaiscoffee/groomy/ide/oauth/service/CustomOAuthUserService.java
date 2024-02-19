package javaiscoffee.groomy.ide.oauth.service;

import javaiscoffee.groomy.ide.login.LoginService;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.oauth.userInfo.CustomOAuthUser;
import javaiscoffee.groomy.ide.oauth.OAuthAttributes;
import javaiscoffee.groomy.ide.oauth.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

/**
 * 사용자가 OAuth2 로그인을 시도하면, 백엔드에서는 OAuth2UserService를 통해 사용자의 인증 요청을 처리
 * OAuth2UserService 인터페이스를 구현하고, 사용자 정보를 가져와서 CustomOAuthUser 객체를 생성하는데 중점을 두고 있는 클래스
 */
@Slf4j
@Service
public class CustomOAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final JpaMemberRepository memberRepository;
    private final LoginService loginService;

    @Autowired
    public  CustomOAuthUserService (JpaMemberRepository memberRepository, @Lazy LoginService loginService) {
        this.memberRepository = memberRepository;
        this.loginService = loginService;

    }

    // DefaultOAuth2UserService = 내부에서 OAuth 서비스와의 통신이 이루어져 사용자 정보를 가져오는 로직이 수행된다.
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOauthUserService.loadUser() 실행 - OAuth 로그인 요청 진입");

        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * userRequest에서 registrationId 추출 후 registrationId로 SocialType 저장
         * http://{baseUrl}/login/oauth2/code/google에서 google이 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         * userNameAttributeName = "sub" : 식별자 값
         * => Spring Security에서는 각 OAuth2.0 공급자(구글 등)별로 사용자의 고유 식별자를 가져오는 데 사용되는 속성 이름을 사전에 정의한다.
         *    이 값을 명시적으로 설정하지 않는 경우에는 구글 OAuth 공급자의 기본 식별자 값이 자동으로 사용된다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();    // OAuth2 로그인 시 키(PK)가 되는 값 (식별자 값)
        Map<String, Object> attributes = oAuth2User.getAttributes();  // 소셜 로그인에서 API가 제공하는 userInfo의 JSON 값(유저 정보들)
        log.info("userInfo 정보 ={}", attributes);

        // socialType에 따라 사용자 정보를 담고 있는 attributes 맵을 받아와 이를 이용하여  OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        log.info("extractAttributes = {}",extractAttributes.getNameAttributeKey());

        Member oauthMember = getUser(extractAttributes, socialType);
        log.info("oauthMember = {}",oauthMember);

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        CustomOAuthUser customOAuthUser = new CustomOAuthUser(
                Collections.singleton(new SimpleGrantedAuthority(oauthMember.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                oauthMember.getEmail(),
                oauthMember.getRole(),
                oauthMember.getMemberId()
        );

        log.info("customOAuthUser = {}",customOAuthUser);

        return customOAuthUser;
    }

    private SocialType getSocialType(String registrationId) {
        return SocialType.GOOGLE;
    }

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
    private Member getUser(OAuthAttributes attributes, SocialType socialType) {
        Member findMember = memberRepository.findByEmail(attributes.getOauthUserInfo().getEmail()).orElse(null);

        if (findMember == null) {
            return loginService.saveUser(attributes, socialType);
        }
        return findMember;
    }
}
