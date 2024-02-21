package javaiscoffee.groomy.ide.login.emailAuthentication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

/**
 * 예시..
 */
@Getter
@Setter
@Entity
@Table(name = "email_certification")
public class EmailVerification {

    @Id
    private String email;
    private String certificationNumber;
    private boolean certificated;
    private LocalDateTime time;

    public EmailVerification(String email, String certificationNumber, LocalDateTime time) {
        this.email = email;
        this.certificationNumber = certificationNumber;
        this.certificated = false;
        this.time = LocalDateTime.now().withNano(0);
    }

}
