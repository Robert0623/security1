package com.course.security1.controller;

import com.course.security1.model.User;
import com.course.security1.repository.UserRepository;
import com.course.security1.request.UserCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    @GetMapping({"", "/"})
    public String index() {
        // mustache 기본 경로 src/main/resources/
        // view resolver 설정 --> templates (prefix), .mustache (suffix) 생략 가능
        return "index";
    }
    @ResponseBody
    @GetMapping("/user")
    public String user() {
        return "user";
    }
    @ResponseBody
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @ResponseBody
    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    // 스프링 시큐리티가 낚아챔
    @GetMapping("/loginForm")
    public String login() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(UserCreate request) {
        String rawPassword = request.getPassword();
        String encryptedPassword = bCryptPasswordEncoder.encode(rawPassword);
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encryptedPassword)
                .role("ROLE_USER")
                .build();
        userRepository.save(user); // 비밀번호 1234 --> 시큐리티로 로그인을 할 수 없음 --> 패스워드 암호화 필요
        return "redirect:/loginForm";
    }
}
