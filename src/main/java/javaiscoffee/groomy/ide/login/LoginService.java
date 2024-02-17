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
@Transactional(readOnly = true)
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
    public TokenDto login(LoginDto loginDto) {
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
            return tokenDto;
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public Member register(RegisterDto registerDto) {
        RegisterDto.Data data = registerDto.getData();
        //이미 중복된 이메일이 존재
        if(memberRepository.findByEmail(data.getEmail()).isPresent()) {
            log.info("중복 회원가입 실패 처리");
            return null;
        }
        //중복이 없으면 회원가입 진행
        Member newMember = new Member(data.getEmail(), data.getPassword(), data.getName(), data.getNickname(),0L, MemberRole.USER);
        newMember.hashPassword(bCryptPasswordEncoder);
        log.info("save하려는 멤버 = {}",newMember);
        memberRepository.save(newMember);
        Optional<Member> savedMember = memberRepository.findByEmail(newMember.getEmail());
        if(savedMember.isPresent()) {
            log.info("회원가입 성공 = {}",savedMember.get());
            return savedMember.get();
        }
        return null;
    }

    /**
     * access 토큰 재발급
     * isTemp = true이면 라이브코딩용 임시 토큰 발급이므로 1분짜리 토큰 발급
     * isTemp = false이면 일반적인 30분 토큰 발급
     */
    public TokenDto refresh(String refreshToken, boolean isTemp) {
        try {
            // refreshToken 유효성 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                // 유효하지 않은 경우, 적절한 응답 반환
                return null;
            }

            // 새로운 AccessToken 생성
            String newAccessToken;
            //라이브 코딩용 1분짜리 임시토큰 발급
            if(isTemp) {
                newAccessToken = jwtTokenProvider.generateNewAccessToken(refreshToken, 1000*60);
            }
            //일반적인 refresh 요청으로 30분짜리 토큰 발급
            else {
                newAccessToken = jwtTokenProvider.generateNewAccessToken(refreshToken, 1000*60*30);
            }

            // 새로운 토큰과 함께 응답 반환
            TokenDto tokenDto = new TokenDto("Bearer", newAccessToken, refreshToken);
            return tokenDto;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 파싱 실패 또는 유효하지 않은 토큰으로 인한 예외 처리
            log.error("토큰 갱신 실패: {}", e.getMessage());
            return null;
        }
    }
}
