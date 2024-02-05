package javaiscoffee.groomy.ide.member;

import javaiscoffee.groomy.ide.response.MyResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@Slf4j
@RequestMapping("/api/member")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 내 정보 조회할 때 사용하는 API
     * 요구 데이터 : 토큰값
     * 반환 데이터 : 토큰값에 해당하는 멤버 정보를 담은 MyResponse
     */
    @GetMapping("/my")
    public MyResponse<MemberInformationDto> getMyProfile(@RequestHeader(value = "Authorization") String token) {
        String email = extractEmail(token);
        log.info("내 정보를 확인하려는 email = {}",email);

        return memberService.getMemberInformation(email);
    }

    /**
     * 마이페이지에서 정보 수정할 때 쓰는 API
     * 요구 데이터 : 패스워드 제외 나머지 수정할 데이터를 포함한 멤버 정보 전체
     * 반환 데이터 : 수정된 정보를 포함한 MyResponse
     */
    @PatchMapping("/my/edit")
    public MyResponse<MemberInformationDto> editMyProfile(@RequestHeader(value = "Authorization") String token, @RequestBody MemberInformationDto memberInformationDto) {
        String email = extractEmail(token);
        log.info("내 정보를 수정하려는 이메일 = {}",email);
        log.info("수정하려는 정보 = {}",memberInformationDto);
        return memberService.updateMemberInformation(email, memberInformationDto);
    }

    /**
     * 마이페이지에서 비밀번호 수정할 때 쓰는 API
     * 요구 데이터 : 패스워드와 토큰
     * 반환 데이터 : 성공했다는 status만 가지고 있는 MyResponse
     */
    @PatchMapping("/my/edit/reset-password")
    public MyResponse<Null> resetPassword(@RequestHeader(value = "Authorization") String token, @RequestBody PasswordResetDto request) {
        String email = extractEmail(token);
        String newPassword = request.getData().getPassword();
        return memberService.resetPassword(email, newPassword);
    }

    // access토큰에서 이메일을 뽑아서 반환
    private String extractEmail(String token) {
        Base64.Decoder decoder = Base64.getDecoder();
        String[] splitJwt = token.split("\\.");
        String payload = new String(decoder.decode(splitJwt[1]
                .replace("-", "+")
                .replace("_", "/")));
        return new String(payload.substring(payload.indexOf("email") + 9, payload.indexOf("com")+3));
    }
}
