package javaiscoffee.groomy.ide.oauth;

import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.member.MemberRole;
import javaiscoffee.groomy.ide.oauth.userInfo.GoogleUserInfo;
import javaiscoffee.groomy.ide.oauth.userInfo.OAuthUserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OAuth 인증 정보를 처리하고,  이를 기반으로 Member 엔티티를 생성하고 저장하는 클래스
 */
@Slf4j
@Getter
public class OAuthAttributes {
    private  String nameAttributeKey; // OAuth 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuthUserInfo oauthUserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuthUserInfo oauthUserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauthUserInfo = oauthUserInfo;
    }

    /**
     * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * 파라미터 : userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값(구글 식별자 값인 "sub") / attributes : OAuth 서비스의 유저 정보들
     * of 메서드는 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
     */
    public static OAuthAttributes of(SocialType socialType, String usernameAttributeName, Map<String, Object> attributes) {
        log.info("of() usernameAttributeName = {}",usernameAttributeName);
        return ofGoogle(usernameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String usernameAttributeName, Map<String, Object> attributes) {
        log.info("ofGoogle() usernameAttributeName = {}",usernameAttributeName);
        return OAuthAttributes.builder()
                .nameAttributeKey(usernameAttributeName)
                .oauthUserInfo(new GoogleUserInfo(attributes))
                .build();
    }

    /**
     * of메서드로 OauthAttributes 객체가 생성되어, 유저 정보들이 담긴 Oauth2serInfo가 주입된 상태
     * OauthUserInfo에서 socialId(식별값), name, email을 가져와서 build
     * role은 USER로 설정
     * (구글 OAuth 사용자의 정보를 내 프로젝트의 Member 테이블에 저장)
     */
    public Member toEntity(SocialType socialType , OAuthUserInfo oauthUserInfo) {
        // password 암호화 => 1.먼저 OAuth로부터 받은 사용자의 정보를 Member에 설정해주고
        Member member = Member.builder()
                .socialType(socialType)
                .password(oauthUserInfo.getId())    // OAuth에서 받아온 식별자 값 : "sub", 사용자마다 고유한 값임
                .email(oauthUserInfo.getEmail())   // OAuth에서 받아온 email
                .name(oauthUserInfo.getName())  //  OAuth에서 받아온 전체 이름
                .role(MemberRole.USER)  // OAuth 로그인 성공 시 회원가입이 아닌 로그인 되게 할 것이므로 바로 USER로 설정
                .build();

        member.setNickname("구르미");  // 닉네임 임의로 지정.

        return member;
    }
}
