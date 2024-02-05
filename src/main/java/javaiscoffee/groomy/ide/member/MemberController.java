package javaiscoffee.groomy.ide.member;

import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/member")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 내 정보 조회할 때 사용하는 API
     * 요구 데이터 : 토큰값에서 뽑아낸 UserDetails
     * 반환 데이터 : 토큰값에 해당하는 멤버 정보를 담은 MyResponse
     */
    @GetMapping("/my")
    public MyResponse<MemberInformationDto> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            log.info("내 정보를 확인하려는 email = {}", email);
            // 여기서 email 변수를 사용하여 필요한 로직을 수행
            return memberService.getMemberInformation(email);
        } else {
            // userDetails가 null인 경우의 처리
            log.error("인증된 사용자가 없음");
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }

    /**
     * 마이페이지에서 정보 수정할 때 쓰는 API
     * 요구 데이터 : 토큰값에서 뽑아낸 UserDetails + 패스워드 제외 나머지 수정할 데이터를 포함한 멤버 정보 전체
     * 반환 데이터 : 수정된 정보를 포함한 MyResponse
     */
    @PatchMapping("/my/edit")
    public MyResponse<MemberInformationDto> editMyProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MemberInformationDto memberInformationDto) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            log.info("내 정보를 확인하려는 email = {}", email);
            log.info("수정하려는 정보 = {}",memberInformationDto);
            // 여기서 email 변수를 사용하여 필요한 로직을 수행
            return memberService.updateMemberInformation(email, memberInformationDto);
        } else {
            // userDetails가 null인 경우의 처리
            log.error("인증된 사용자가 없음");
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }

    /**
     * 마이페이지에서 비밀번호 수정할 때 쓰는 API
     * 요구 데이터 : 패스워드와 토큰
     * 반환 데이터 : 성공했다는 status만 가지고 있는 MyResponse
     */
    @PatchMapping("/my/edit/reset-password")
    public MyResponse<Null> resetPassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PasswordResetDto passwordResetDto) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            log.info("비밀번호 재설정하려는 email = {}", email);
            // 여기서 email 변수를 사용하여 필요한 로직을 수행
            String newPassword = passwordResetDto.getData().getPassword();
            return memberService.resetPassword(email, newPassword);
        } else {
            // userDetails가 null인 경우의 처리
            log.error("인증된 사용자가 없음");
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }
}
