package com.course.security1.controller;

import com.course.security1.config.auth.PrincipalDetails;
import com.course.security1.model.User;
import com.course.security1.repository.UserRepository;
import com.course.security1.request.UserCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @ResponseBody
    @GetMapping("/test/login")
    public String loginTest(Authentication authentication,
                            @AuthenticationPrincipal PrincipalDetails userDetails) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
        return "세션 정보 확인하기";
    }

    @ResponseBody
    @GetMapping("/test/oauth/login")
    public String loginOauthTest(Authentication authentication,
                                 @AuthenticationPrincipal OAuth2User oauth) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
        System.out.println("oauth = " + oauth.getAttributes());
        return "OAuth세션 정보 확인하기";
    }

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

    @ResponseBody
    // 기본적으로 url 패턴으로 체크 하고, 그 이외에 하나씩 권한체크를 하고싶을 때
    @Secured("ROLE_ADMIN") // 하나의 권한을 체크
    @GetMapping("/info")
    public String info() {
        return "개인정보";
    }

    @ResponseBody
    // 기본적으로 url 패턴으로 체크 하고, 그 이외에 하나씩 권한체크를 하고싶을 때
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 여러 개의 권한을 체크
    @GetMapping("/data")
    public String data() {
        return "데이터";
    }
}
