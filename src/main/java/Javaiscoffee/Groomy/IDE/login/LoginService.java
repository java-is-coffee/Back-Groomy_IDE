package Javaiscoffee.Groomy.IDE.login;

import Javaiscoffee.Groomy.IDE.member.JpaMemberRepository;
import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.member.MemberRole;
import Javaiscoffee.Groomy.IDE.response.MyResponse;
import Javaiscoffee.Groomy.IDE.response.ResponseStatus;
import Javaiscoffee.Groomy.IDE.response.Status;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final JpaMemberRepository repository;
    private final PasswordEncoder bCryptPasswordEncoder;
    public MyResponse<Member> login(LoginDto loginDto) {
        Member loginMember = repository.findByEmail(loginDto.getData().getEmail()).get();
        // null이 도착하면 로그인 실패
        if(loginMember != null) {
            //비밇번호 같은지 확인 후 통과하면 멤버 반환
            if(loginMember.checkPassword(loginDto.getData().getPassword(),bCryptPasswordEncoder)) {
                return new MyResponse<>(new Status(ResponseStatus.SUCCESS), loginMember);
            }
            //비밀번호가 틀렸을 시 실패 response 반환
            return new MyResponse<>(new Status(ResponseStatus.PASSWORD_INCORRECT), null);
        }
        return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND), null);
    }
    public MyResponse<Null> register(RegisterDto registerDto) {
        RegisterDto.Data data = registerDto.getData();
        //이미 중복된 이메일이 존재
        if(repository.findByEmail(data.getEmail()).isPresent()) {
            log.info("중복 회원가입 실패 처리");
            return new MyResponse<>(new Status(ResponseStatus.REGISTER_DUPLICATED));
        }
        //중복이 없으면 회원가입 진행
        Member newMember = new Member(data.getEmail(), data.getPassword(), data.getName(), data.getNickname(),0L, MemberRole.USER);
        newMember.hashPassword(bCryptPasswordEncoder);
        repository.save(newMember);
        Optional<Member> saveMember = repository.findByEmail(newMember.getEmail());
        if(saveMember.isPresent()) {
            log.info("회원가입 성공 = {}",saveMember.get());
            return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
        }
        return new MyResponse<>(new Status(ResponseStatus.REGISTER_FAILED));
    }
}
