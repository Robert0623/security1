package com.course.security1.config;

import com.course.security1.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨
//@EnableMethodSecurity(securedEnabled = true) // @Secured
//@EnableMethodSecurity // @PreAuthorize (default)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").authenticated() // 인증(로그인) 필요
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") // 인증과 role 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 인증과 role 필요
                        .anyRequest().permitAll()) // 나머지 인증(로그인) 필요 X
                .csrf(csrf -> csrf
                        .disable())
                .formLogin(form -> form
                        .loginPage("/loginForm")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/"))
                // 구글 로그인이 완료된 뒤의 후처리가 필요함.
                // 1.코드받기(인증), 2.엑세스토큰(권한), 3.사용자프로필 정보를 가져오고,
                // 4-1.그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
                // 4-2.(이메일,전화번호,이름,아이디) 쇼핑몰 --> (집주소), 백화점몰--> (vip등급,일반등급)
                // Tip. 코드 X --> (엑세스토큰 + 사용자프로필정보를 받는다)
                .oauth2Login(oauth -> oauth
                        .loginPage("/loginForm")
                        .userInfoEndpoint(info -> info
                                .userService(principalOauth2UserService))
                )
                .build();
    }

}
