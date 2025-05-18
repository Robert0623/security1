package com.course.security1.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글 로그인 후 후처리 되는 함수 --> 강제 회원가입
    // username = google_128391231231123 (google_${sub})
    // password = 암호화(겟인데어)
    // email = aaa@aaa.com
    // role = ROLE_USER
    // provider = gogole
    // providerId = 128391231231123
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return super.loadUser(userRequest);
    }
}