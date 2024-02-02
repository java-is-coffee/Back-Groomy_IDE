package Javaiscoffee.Groomy.IDE.controller;

import Javaiscoffee.Groomy.IDE.member.JpaMemberRepository;
import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final JpaMemberRepository repository;
    @GetMapping("/test")
    public Member test() {
        Member member = new Member("tkrhkrkfn@naver.com", "aaaa", "박상현", "박박긁어");
        repository.save(member);
        return member;
    }
}
