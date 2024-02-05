package javaiscoffee.groomy.ide.member;


import lombok.Data;

@Data
public class MemberInformationDto {
    private Data data;

    @lombok.Data
    public static class Data {
        private Long memberId;
        private String email;
        private String name;
        private String nickname;
        private Long helpNumber;
        private MemberRole role;
    }
}
