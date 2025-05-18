package com.course.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨
//@EnableMethodSecurity(securedEnabled = true) // @Secured
//@EnableMethodSecurity // @PreAuthorize (default)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").authenticated() // 인증(로그인) 필요
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") // 인증과 role 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 인증과 role 필요
                        .anyRequest().permitAll()) // 나머지 인증(로그인) 필요 X
                .csrf(csrf -> csrf.disable())
                .formLogin(form ->
                        form.loginPage("/loginForm")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/"))
                .build();
    }

}
