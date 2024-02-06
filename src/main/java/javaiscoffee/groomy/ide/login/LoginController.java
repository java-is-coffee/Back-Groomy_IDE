package javaiscoffee.groomy.ide.login;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import javaiscoffee.groomy.ide.security.RefreshTokenDto;
import javaiscoffee.groomy.ide.security.TokenDto;
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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        log.info("로그인 요청");
        TokenDto tokenDto = loginService.login(loginDto);
        //로그인 실패했을 경우 실패 Response 반환
        if (tokenDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        log.info("registerDto = {}", registerDto);
        Member registerdMember = loginService.register(registerDto);
        if(registerdMember==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.REGISTER_FAILED));
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getData().getRefreshToken();
        log.info("refreshToken 받음 = {}", refreshToken);
        //토큰 검증 후 토큰 받아오기
        TokenDto tokenDto = loginService.refresh(refreshToken);
        if(tokenDto==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.UNAUTHORIZED));
        }
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/register/email-check")
    public ResponseEntity<Boolean> emailCheck(@RequestBody EmailCheckDto emailCheckDto) {
        log.info("이메일 체크 진입 = {}", emailCheckDto.getData().getEmail());
        return ResponseEntity.ok(memberRepository.existsByEmail(emailCheckDto.getData().getEmail()));
    }

}
