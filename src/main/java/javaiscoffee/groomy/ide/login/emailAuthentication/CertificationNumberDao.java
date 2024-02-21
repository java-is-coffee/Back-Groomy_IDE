package javaiscoffee.groomy.ide.login.emailAuthentication;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

// Redis 사용해서 이메일 인증 번호를 저장하고 관리하는 DAO(Data Access Object) 클래스
// DB로 변경할 것
@Repository
@RequiredArgsConstructor
public class CertificationNumberDao {
    // Spring Data Redis에서 제공하는 클래스, Redis 데이터 처리를 위한 기능을 제공
    private final StringRedisTemplate redisTemplate;

    /**
     * 이메일과 인증 번호는 Redis에 저장
     * key = email
     * value = 인증 번호
     * Duration.ofSeconds() 인증 번호의 유효 기간
     */
    public void saveCertification(String email, String certificationNumber, int time) {
        redisTemplate.opsForValue()
                .set(email, certificationNumber, Duration.ofSeconds(time)); // 5분
    }

    // 이메일에 대한 인증 번호를 Redis에서 get
    public String getCertificationNumber(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    // 이메일에 대한 인증 번호를 Redis에서 삭제
    public void removeCertificationNumber(String email) {
        redisTemplate.delete(email);
    }

    /**
     * 이메일을 키로 가지는 데이터가 Redis에 존재하는지 여부 확인.
     * @return 키가 존재하면 true, 존재하지 않으면 false
     */
    public boolean hasKey(String email) {
        Boolean keyExists = redisTemplate.hasKey(email);
        return keyExists != null && keyExists;
    }
}
