package javaiscoffee.groomy.ide.login.oauth.userInfo;

import java.util.Map;

/**
 * 구글은 유저 정보가 감싸져 있지 않기 때문에 바로 get으로 유저 정보 key를 사용해서 꺼내면 된다.
 */
public class GoogleUserInfo extends OAuthUserInfo {
    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
