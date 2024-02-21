package javaiscoffee.groomy.ide.login.emailAuthentication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 예시..
 */
@Getter
@Setter
@Entity
@Table(name = "email_certification")
@NoArgsConstructor
public class EmailVerification {

    @Id
    private String email;
    private String certificationNumber;
    private Boolean certificated;
    private LocalDateTime createdTime;
    private LocalDateTime expirationTime;

    public EmailVerification(String email, String certificationNumber, LocalDateTime time) {
        this.email = email;
        this.certificationNumber = certificationNumber;
        this.certificated = false;
        this.expirationTime = time;
        this.createdTime = LocalDateTime.now();
    }

}
