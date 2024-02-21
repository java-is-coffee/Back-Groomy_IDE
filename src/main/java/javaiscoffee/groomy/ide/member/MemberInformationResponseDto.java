package javaiscoffee.groomy.ide.member;

import javaiscoffee.groomy.ide.login.oauth.SocialType;
import lombok.Data;

/**
 * 유저 정보 조회할 때 응답으로 주는 클래스
 */
@Data
public class MemberInformationResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String nickname;
    private Long helpNumber;
    private MemberRole role;
    private SocialType socialType;
}
