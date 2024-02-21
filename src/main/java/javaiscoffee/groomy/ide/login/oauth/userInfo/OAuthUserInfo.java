package javaiscoffee.groomy.ide.login.oauth.userInfo;

import java.util.Map;

public abstract class OAuthUserInfo {
    protected Map<String, Object> attributes;

    public OAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 소셜 식별 값 : 구글 - "sub" => 패스워드 칠 필요 없으니까 암호화해서 패스워드로 저장
    public abstract String getName(); // 이름 칸에 들어갈 것
    public abstract String getEmail();

}
