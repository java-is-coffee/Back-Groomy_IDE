package javaiscoffee.groomy.ide.login;

import jakarta.validation.Valid;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import javaiscoffee.groomy.ide.security.RefreshTokenDto;
import javaiscoffee.groomy.ide.security.TokenDto;
import javaiscoffee.groomy.ide.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 나중에 정식 배포하기 전에
 * 컨트롤러 파라미터에 검증해야하는 DTO에 @Valid 추가하기
 */

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final JpaMemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestWrapperDto<LoginDto> requestDto) {
        LoginDto loginDto = requestDto.getData();
        log.info("로그인 요청");
        TokenDto tokenDto = loginService.login(loginDto);
        //로그인 실패했을 경우 실패 Response 반환
        if (tokenDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestWrapperDto<RegisterDto> requestDto) {
        RegisterDto registerDto = requestDto.getData();
        log.info("registerDto = {}", registerDto);
        Member registerdMember = loginService.register(registerDto);
        if(registerdMember==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.REGISTER_FAILED));
        }
        return ResponseEntity.ok(null);
    }

    /**
     * access 토큰 30분짜리 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getData().getRefreshToken();
        log.info("refreshToken 받음 = {}", refreshToken);
        //토큰 검증 후 30분짜리 일반 토큰 받아오기
        TokenDto tokenDto = loginService.refresh(refreshToken,false);
        if(tokenDto==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.UNAUTHORIZED));
        }
        return ResponseEntity.ok(tokenDto);
    }

    /**
     * 라이브 코딩용 임시 토큰 발급
     * refresh랑 똑같은데 1분짜리 토큰 발급만 다름
     */
    @PostMapping("/tempToken")
    public ResponseEntity<?> getTempAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getData().getRefreshToken();
        log.info("refreshToken 받음 = {}", refreshToken);
        //토큰 검증 후 30분짜리 일반 토큰 받아오기
        TokenDto tokenDto = loginService.refresh(refreshToken,true);
        if(tokenDto==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.UNAUTHORIZED));
        }
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/register/email-check")
    public ResponseEntity<Boolean> emailCheck( @RequestBody RequestWrapperDto<EmailCheckDto> requestDto) {
        EmailCheckDto emailCheckDto = requestDto.getData();
        log.info("이메일 체크 진입 = {}", emailCheckDto.getEmail());
        return ResponseEntity.ok(memberRepository.existsByEmail(emailCheckDto.getEmail()));
    }

    @PostMapping("/login/reset-password")
    public ResponseEntity<Boolean> resetPassword(@RequestBody RequestWrapperDto<ResetPasswordRequestDto> wrapperDto) {
        ResetPasswordRequestDto requestDto = wrapperDto.getData();
        loginService.resetPassword(requestDto);
        return ResponseEntity.ok(null);
    }

}
