package javaiscoffee.groomy.ide.member;

import lombok.Data;

@Data
public class MemberInformationResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String nickname;
    private Long helpNumber;
    private MemberRole role;
}
