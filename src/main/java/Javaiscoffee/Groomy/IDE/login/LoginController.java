package Javaiscoffee.Groomy.IDE.login;

import Javaiscoffee.Groomy.IDE.member.JpaMemberRepository;
import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.member.MemberRepository;
import Javaiscoffee.Groomy.IDE.member.MemberRole;
import Javaiscoffee.Groomy.IDE.response.MyResponse;
import Javaiscoffee.Groomy.IDE.response.ResponseStatus;
import Javaiscoffee.Groomy.IDE.response.Status;
import Javaiscoffee.Groomy.IDE.security.JwtTokenProvider;
import Javaiscoffee.Groomy.IDE.security.RefreshTokenDto;
import Javaiscoffee.Groomy.IDE.security.TokenDto;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final JpaMemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<MyResponse<TokenDto>> login(@RequestBody LoginDto loginDto) {
        log.info("로그인 요청");
        MyResponse<TokenDto> response = loginService.login(loginDto);
        //로그인 실패했을 경우 실패 Response 반환
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public MyResponse<Null> register(@RequestBody RegisterDto registerDto) {
        log.info("registerDto = {}", registerDto);
        return loginService.register(registerDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<MyResponse<TokenDto>> refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getData().getRefreshToken();
        log.info("refreshToken 받음 = {}", refreshToken);
        //토큰 검증 후 토큰 받아오기
        MyResponse<TokenDto> myResponse = loginService.refresh(refreshToken);
        log.info("tokenDto 내용", myResponse.getData());

        return ResponseEntity.ok(myResponse);
    }

    @PostMapping("/register/email-check")
    public MyResponse<EmailCheckResultDto> emailCheck(@RequestBody EmailCheckDto emailCheckDto) {
        log.info("이메일 체크 진입 = {}", emailCheckDto.getData().getEmail());
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), new EmailCheckResultDto().setDuplicated(memberRepository.existsByEmail(emailCheckDto.getData().getEmail())));
    }

}
