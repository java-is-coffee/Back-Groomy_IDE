package Javaiscoffee.Groomy.IDE.member;

import Javaiscoffee.Groomy.IDE.login.LoginDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    public Optional<Member> findByEmail(String email);  //이메일로 Member 찾기
    boolean existsByEmail(String email);    //이메일로 Member 존재 여부 확인

    public void save(Member newMember);
}

