package javaiscoffee.groomy.ide.login.emailAuthentication;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Table(name = "email_certification")
public class JpaEmailCertificationRepository implements EmailCertificationRepository{
    private final EntityManager em;
    public JpaEmailCertificationRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void saveCertification(String email, String certificationNumber, int time) {
        // 현재 시간을 가져온 후에 time 매개변수로 전달된 분 만큼 더해 유효 시간을 설정
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(time);
        EmailVerification emailVerification = new EmailVerification(email,certificationNumber,expirationTime);
        em.persist(emailVerification);
    }

    // 이메일
    @Override
    public EmailVerification findEmailVerificationByEmail(String email) {
        return em.find(EmailVerification.class,email);
    }

    // 삭제
    @Override
    public void removeEmailVerificationNumber(String email) {
        // 이메일 객체를 찾아서 주입해서 삭제
        EmailVerification emailVerification = findEmailVerificationByEmail(email);
        em.remove(emailVerification);
    }

    // 인증 완료로 변경
    @Override
    public void certificateSuccess(String email) {
        EmailVerification verification = em.find(EmailVerification.class,email);
        verification.setCertificated(true);
    }

    @Override
    public List<EmailVerification> findByTimeBeforeAndCertificatedIsFalse() {
        return em.createQuery("SELECT e FROM EmailVerification e WHERE e.expirationTime < :time", EmailVerification.class)
                .setParameter("time", LocalDateTime.now())
                .getResultList();
    }

    @Override
    public void deleteAll(List<EmailVerification> verifications) {
        for (EmailVerification verification : verifications) {
            EmailVerification attachedEntity = em.find(EmailVerification.class, verification.getEmail());
            if (attachedEntity != null) {
                em.remove(attachedEntity);
            }
        }
    }
}
