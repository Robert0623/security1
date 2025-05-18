package com.course.security1.config.auth;

import com.course.security1.model.User;
import com.course.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시ㅠ리티 설정에서 loginProcessingUrl("/login")'
// POST "/login" 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어있는 loadUserByUsername 함수가 실행.

// 시큐리티 session(내부 Authentication(내부 UserDetails))
// 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(IllegalArgumentException::new);

        return new PrincipalDetails(user);
    }
}