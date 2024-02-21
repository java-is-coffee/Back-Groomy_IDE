package javaiscoffee.groomy.ide.login.emailAuthentication;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

// 사용자가 링크 클릭 후 이메일에 대한 인증 코드가 일치하는지 확인하는 클래스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailVerifyService {
    private final JpaEmailCertificationRepository emailCertificationRepository;
    private final MailSendService mailSendService;


    /**
     * 홈페이지에서 인증번호 인증 버튼을 클릭하면 호출되는 메서드
     */
    @Transactional
    public boolean verifyEmail(String email, String certificationNumber) {
        //
        if (!checkVerification(email, certificationNumber)) {
            return false;
        }
        // 해당 이메일 인증 여부 true로 설정
        emailCertificationRepository.certificateSuccess(email);

        return true;
    }

    /**
     * 회원가입 직전에 확인하는 용도
     * 사용자의 이메일, 입력한 인증 번호 가져와서 처리
     */
    public boolean isVerified(String email, String certificationNumber) {
        EmailVerification verification = emailCertificationRepository.findEmailVerificationByEmail(email);
        //이메일 인증을 한 적이 없으면 || 키와 밸류가 일치하지 않으면 || 유효 시간이 지났으면 => 예외처리
        if (verification == null || !verification.getCertificated() || isTimeout(verification.getExpirationTime())) {
            return false;
        }
//        emailCertificationRepository.removeEmailVerificationNumber(email);
        return true;
    }

    /**
     * 회원가입할 때 이메일 인증 번호 입력하고 인증할 때 사용
     * 이메일 인증번호를 입력해서 인증 시간 초과 안했는지 + 인증 번호가 맞는지 체크 => 인증 유무 true로 업데이트
     */
    private boolean checkVerification(String email, String certificationNumber) {
        EmailVerification verification = emailCertificationRepository.findEmailVerificationByEmail(email);
        // 인증 번호가 null이 아니고, 입력된 인증 번호와 저장된 인증 번호가 같고 || 인증 시간이 안 지났으면 true 반환
        return verification != null && verification.getCertificationNumber().equals(certificationNumber) && !isTimeout(verification.getExpirationTime());
    }

    /**
     * 유효 시간 지났으면 true 반환
     * 유효 시간 안지나서 가입 가능하면 false 반환
     */
    private boolean isTimeout(LocalDateTime expirationTime) {
        return expirationTime.isBefore(LocalDateTime.now());
    }

}
