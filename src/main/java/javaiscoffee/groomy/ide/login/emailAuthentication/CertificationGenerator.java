package javaiscoffee.groomy.ide.login.emailAuthentication;

import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// 난수를 만드는 클래스
@Component
public class CertificationGenerator {

    // 인증 번호를 생성하는 메서드
    public String createCertificationNumber() throws NoSuchAlgorithmException {
        String result;

        // SecureRandom 클래스를 사용하여 난수를 안전하게 생성할 수 있음
        do {
            int num = SecureRandom.getInstanceStrong().nextInt(999999);
            result = String.valueOf(num);
        } while (result.length() != 6);


        return result;
    }
}
