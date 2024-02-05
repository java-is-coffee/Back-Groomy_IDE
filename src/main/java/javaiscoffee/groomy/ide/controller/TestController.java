package javaiscoffee.groomy.ide.controller;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
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
    public String test() {
        return "success";
    }
}
