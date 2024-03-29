package javaiscoffee.groomy.ide.login;

import jakarta.mail.MessagingException;
import javaiscoffee.groomy.ide.login.emailAuthentication.CertificationGenerator;
import javaiscoffee.groomy.ide.login.emailAuthentication.JpaEmailCertificationRepository;
import javaiscoffee.groomy.ide.login.emailAuthentication.MailSendService;
import javaiscoffee.groomy.ide.login.emailAuthentication.MailVerifyService;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.member.MemberRole;
import javaiscoffee.groomy.ide.login.oauth.OAuthAttributes;
import javaiscoffee.groomy.ide.login.oauth.SocialType;
import javaiscoffee.groomy.ide.member.MemberStatus;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import javaiscoffee.groomy.ide.security.TokenDto;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {
    private final JpaMemberRepository memberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailVerifyService mailVerifyService;
    private final JpaEmailCertificationRepository emailCertificationRepository;
    private final CertificationGenerator certificationGenerator;
    private final MailSendService mailSendService;

    /**
     * 1. 로그인 요청으로 들어온 memberId, password를 기반으로 Authentication 객체를 생성한다.
     * 2. authenticate() 메서드를 통해 요청된 Member에 대한 검증이 진행된다.
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성한다.
     */
    public TokenDto login(LoginDto loginDto) {
        log.info("로그인 검사 시작 loginDto={}",loginDto);
        Member member = memberRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new BaseException(ResponseStatus.NOT_FOUND.getMessage()));
        if(member.getStatus() == MemberStatus.DELETED) throw new BaseException(ResponseStatus.NOT_FOUND.getMessage());
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
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
        //이미 중복된 이메일이 존재
        if(memberRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            log.info("중복 회원가입 실패 처리");
            return null;
        }

        //이메일 인증한 적이 없으면 예외처리
        if (!mailVerifyService.isVerified(registerDto.getEmail(), registerDto.getCertificationNumber())) {
            log.info("이메일 인증을 하지 않았습니다.");
            return null;
        }


        //중복이 없으면 회원가입 진행
        Member newMember = new Member(registerDto.getEmail(), registerDto.getPassword(), registerDto.getName(), registerDto.getNickname(),0L, MemberRole.USER);
        newMember.hashPassword(bCryptPasswordEncoder);
        log.info("save하려는 멤버 = {}",newMember);
        memberRepository.save(newMember);
        Optional<Member> savedMember = memberRepository.findByEmail(newMember.getEmail());
        if(savedMember.isPresent()) {
            log.info("회원가입 성공 = {}",savedMember.get());
            emailCertificationRepository.removeEmailVerificationNumber(registerDto.getEmail());
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

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장
     */
    public Member saveUser(OAuthAttributes attributes, SocialType socialType) {
        Member createdMember = attributes.toEntity(socialType, attributes.getOauthUserInfo());
        createdMember.hashPassword(bCryptPasswordEncoder);
        return memberRepository.saveOAuthUser(createdMember);
    }

    /**
     * 로그인 페이지 비밀번호 리셋
     */
    @Transactional
    public void resetPassword(ResetPasswordRequestDto requestDto){
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new BaseException(ResponseStatus.BAD_REQUEST.getMessage()));
        if(!Objects.equals(member.getName(), requestDto.getName())) {
            log.error("비밀번호 리셋 이름 틀림");
            throw new BaseException(ResponseStatus.BAD_REQUEST.getMessage());
        }

        String tempPassword;
        try {
            tempPassword = certificationGenerator.createCertificationNumber();
        } catch (NoSuchAlgorithmException e) {
            log.error("임시 비밀번호 발급 실패");
            throw new BaseException(ResponseStatus.ERROR.getMessage());
        }
        member.setPassword(tempPassword);
        member.hashPassword(bCryptPasswordEncoder);

        String mailContent = String.format("%s의 비밀번호 리셋을 위해 발송된 메일입니다.%n임시 비밀번호는   :   %s%n임시 비밀번호를 사용하여 로그인해주세요.%n로그인하고 비밀번호 변경 부탁드립니다.",requestDto.getEmail(),tempPassword);

        try {
            mailSendService.sendMail(member.getEmail(),mailContent);
        } catch (MessagingException e) {
            throw new BaseException(ResponseStatus.ERROR.getMessage());
        }
    }
}
