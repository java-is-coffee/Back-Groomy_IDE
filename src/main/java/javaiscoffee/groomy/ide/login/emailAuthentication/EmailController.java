package javaiscoffee.groomy.ide.login.emailAuthentication;

import jakarta.mail.MessagingException;
import javaiscoffee.groomy.ide.wrapper.RequestWrapperDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private final MailSendService mailSendService;
    private final MailVerifyService mailVerifyService;

    // 인증 요청 버튼 클릭
    // 인증 요청
    @PostMapping("/send-certification")
    public ResponseEntity<?> sendCertificationNumber(@Validated @RequestBody RequestWrapperDto<EmailCertificationRequest> requestDto) throws MessagingException, NoSuchAlgorithmException {
        EmailCertificationRequest request = requestDto.getData();
        log.info(">> 사용자의 이메일 인증 요청");
        mailSendService.sendEmailForCertification(request.getEmail());
        return ResponseEntity.ok(null);
    }

    // 인증 확인 버튼 클릭
    // 인증 확인 : email, certificationNumber 넘겨 받기
    @GetMapping("/verify")
    public ResponseEntity<?> verifyCertificationNumber(@RequestParam(name = "email") String email,
                                                       @RequestParam(name = "certificationNumber") String certificationNumber) {
        log.info(">> 사용자의 이메일 검증 시작");
        boolean verifyEmail = mailVerifyService.verifyEmail(email, certificationNumber);
        if (verifyEmail) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패하였습니다.");
        }
    }
}
