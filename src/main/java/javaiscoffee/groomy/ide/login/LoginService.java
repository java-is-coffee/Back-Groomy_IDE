package javaiscoffee.groomy.ide.login;

import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.member.MemberRepository;
import javaiscoffee.groomy.ide.member.MemberRole;
import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import javaiscoffee.groomy.ide.security.TokenDto;
import io.jsonwebtoken.JwtException;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 1. 로그인 요청으로 들어온 memberId, password를 기반으로 Authentication 객체를 생성한다.
     * 2. authenticate() 메서드를 통해 요청된 Member에 대한 검증이 진행된다.
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성한다.
     */
    @Transactional
    public MyResponse<TokenDto> login(LoginDto loginDto) {
        log.info("로그인 검사 시작 loginDto={}",loginDto);
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getData().getEmail(), loginDto.getData().getPassword());
        log.info("authenticationToken = {}",authenticationToken);
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("Authentication successful, authentication = {}", authentication);

            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            log.info("로그인 성공, tokenDto={}", tokenDto);
            return new MyResponse<>(new Status(ResponseStatus.SUCCESS), tokenDto);
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return new MyResponse<>(new Status(ResponseStatus.LOGIN_FAILED), null);
        }
    }

    public MyResponse<Null> register(RegisterDto registerDto) {
        RegisterDto.Data data = registerDto.getData();
        //이미 중복된 이메일이 존재
        if(memberRepository.findByEmail(data.getEmail()).isPresent()) {
            log.info("중복 회원가입 실패 처리");
            return new MyResponse<>(new Status(ResponseStatus.REGISTER_DUPLICATED));
        }
        //중복이 없으면 회원가입 진행
        Member newMember = new Member(data.getEmail(), data.getPassword(), data.getName(), data.getNickname(),0L, MemberRole.USER);
        newMember.hashPassword(bCryptPasswordEncoder);
        memberRepository.save(newMember);
        Optional<Member> saveMember = memberRepository.findByEmail(newMember.getEmail());
        if(saveMember.isPresent()) {
            log.info("회원가입 성공 = {}",saveMember.get());
            return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
        }
        return new MyResponse<>(new Status(ResponseStatus.REGISTER_FAILED));
    }

    public MyResponse<TokenDto> refresh(String refreshToken) {
        try {
            // refreshToken 유효성 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                // 유효하지 않은 경우, 적절한 응답 반환
                return new MyResponse<>(new Status(ResponseStatus.UNAUTHORIZED));
            }

            // 새로운 AccessToken 생성
            String newAccessToken = jwtTokenProvider.generateNewAccessToken(refreshToken);

            // 새로운 토큰과 함께 응답 반환
            TokenDto tokenDto = new TokenDto("Bearer", newAccessToken, refreshToken);
            return new MyResponse<>(new Status(ResponseStatus.SUCCESS), tokenDto);
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 파싱 실패 또는 유효하지 않은 토큰으로 인한 예외 처리
            log.error("토큰 갱신 실패: {}", e.getMessage());
            return new MyResponse<>(new Status(ResponseStatus.UNAUTHORIZED));
        }
    }
}
