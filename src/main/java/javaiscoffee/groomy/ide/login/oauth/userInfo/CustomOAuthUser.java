package javaiscoffee.groomy.ide.login.oauth.userInfo;

import javaiscoffee.groomy.ide.member.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuthUser extends DefaultOAuth2User {
    private String email;
    private MemberRole role;
    private String name;
    private Long memberId;


    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     *
     * @param authorities      the authorities granted to the user
     * @param attributes       the attributes about the user 유저 정보
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
     *                         {@link #getAttributes()} 식별자 값
     */
    public CustomOAuthUser(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey,
                           String email, MemberRole role, Long memberId/**,String nickname,String password*/) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
        this.memberId = memberId;
        // attributes 맵에서 nameAttributeKey에 해당하는 실제 사용자 이름을 찾아 name 필드에 할당
        this.name = (String) attributes.getOrDefault(nameAttributeKey, null);
    }
}

