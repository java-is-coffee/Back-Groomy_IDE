package Javaiscoffee.Groomy.IDE.login;

import Javaiscoffee.Groomy.IDE.member.JpaMemberRepository;
import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.member.MemberRepository;
import Javaiscoffee.Groomy.IDE.member.MemberRole;
import Javaiscoffee.Groomy.IDE.response.MyResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public MyResponse<Member> login(@RequestBody LoginDto loginDto) {
        log.info("로그인 요청");
        return loginService.login(loginDto);
    }

    @PostMapping("/register")
    public MyResponse<Null> register(@RequestBody RegisterDto registerDto) {
        log.info("registerDto = {}", registerDto);
        return loginService.register(registerDto);
    }

}
