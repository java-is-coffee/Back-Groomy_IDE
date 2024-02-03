package javaiscoffee.groomy.ide.member;

import java.util.Optional;

public interface MemberRepository {
    public Optional<Member> findByEmail(String email);  //이메일로 Member 찾기
    boolean existsByEmail(String email);    //이메일로 Member 존재 여부 확인

    //이메일로 Member 찾기 spring security 위해 추가
    Optional<Member> findByMemberId(String username);

    public void save(Member newMember);
}


