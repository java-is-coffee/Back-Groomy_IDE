package javaiscoffee.groomy.ide.login.emailAuthentication;

public interface EmailCertificationRepository {
    void saveCertification(String email, String certificationNumber, int time);

    // 이메일로 인증 번호 찾기
    EmailVerification findCertificationNumberByEmail(String email);

    void removeCertificationNumber(String email);

    // 인증 했는지 안 헀는지
    void certificateSuccess(String email);
}

