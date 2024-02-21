package javaiscoffee.groomy.ide.login.emailAuthentication;

import javaiscoffee.groomy.ide.security.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사용자가 링크 클릭 후 이메일에 대한 인증 코드가 일치하는지 확인하는 클래스
@Service
@RequiredArgsConstructor
@Transactional
public class MailVerifyService {
    private final JpaEmailCertificationRepository emailCertificationRepository;


    /**
     * 홈페이지에서 인증번호 인증 버튼을 클릭하면 호출되는 메서드
     */
    public boolean verifyEmail(String email, String certificationNumber) {
        // 기존 삭제
        if (!isVerify(email, certificationNumber)) {
            return false;
        }
        // 해당 이메일 인증 여부 true로 설정
        emailCertificationRepository.certificateSuccess(email);
        return true;
    }

    /**
     * 사용자의 이메일, 입력한 인증 번호 가져와서 처리
     */
    public boolean isVerify(String email, String certificationNumber) {
        boolean validatedEmail = isEmailExists(email);
        //이메일 인증을 한 적이 없으면 예외 처리 || 키와 밸류가 일치하지 않으면 예외 처리
        if (!validatedEmail || !emailCertificationRepository.findCertificationNumberByEmail(email).equals(certificationNumber)) {
            return false;
        }
        emailCertificationRepository.removeCertificationNumber(email);
        return true;
    }

    // 이메일로 인증 번호 조회
    private boolean isEmailExists(String email) {
        EmailVerification keyExists = emailCertificationRepository.findCertificationNumberByEmail(email);
        // 인증 번호가 null이 아니고, 입력된 인증 번호와 저장된 인증 번호가 같으면 true 반환
        return keyExists != null && keyExists.equals(keyExists);
    }

    /**
     * 유효 시간 지났으면 true 반환
     * 유효 시간 안지나서 가입 가능하면 false 반환
     */
    private boolean isTimeout(LocalDateTime expirationTime) {
        return expirationTime.isBefore(LocalDateTime.now());
    }

}
