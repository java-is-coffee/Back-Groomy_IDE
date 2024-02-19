package javaiscoffee.groomy.ide.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    /**
     * 일반적으로 "ROLE_"을 붙이는 것이 관례이다. (명시적으로 역할을 부여할 때는 붙이는 것이 좋다.)
     * Spring Security에서 강제하는 것은 아니기 때문에 붙이지 않고도 사용자 정의 역할을 사용 할 수 있다.( 인식 가능 )
     * 붙이지 않았을 경우 Spring Security에서 자동으로 "ROLE_"을 붙여주지는 않는다.
     * --- 공식 문서 ---
     * By default, role-based authorization rules include ROLE_ as a prefix.
     * This means that if there is an authorization rule that requires a security context to have a role of "USER",
     * Spring Security will by default look for a GrantedAuthority#getAuthority that returns "ROLE_USER".
     */
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), GUEST("ROLE_GUEST");
    private final String key;
}
