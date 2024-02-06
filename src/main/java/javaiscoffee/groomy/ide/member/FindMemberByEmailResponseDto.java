package javaiscoffee.groomy.ide.member;

import lombok.Data;

/**
 * 유저 정보 조회할 때 응답으로 주는 클래스
 */
@Data
public class FindMemberByEmailResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
}
