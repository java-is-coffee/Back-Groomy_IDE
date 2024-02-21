package javaiscoffee.groomy.ide.login.emailAuthentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailCertificationRepository {
    void saveCertification(String email, String certificationNumber, int time);

    // 이메일로 인증 번호 찾기
    EmailVerification findEmailVerificationByEmail(String email);

    void removeEmailVerificationNumber(String email);

    // 인증 했는지 안 헀는지
    void certificateSuccess(String email);

    List<EmailVerification> findByTimeBeforeAndCertificatedIsFalse();

    void deleteAll(List<EmailVerification> list);
}

