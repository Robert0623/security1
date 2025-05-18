package com.course.security1.config.oauth;

import com.course.security1.config.auth.PrincipalDetails;
import com.course.security1.config.auth.provider.FacebookUserInfo;
import com.course.security1.config.auth.provider.GoogleUserInfo;
import com.course.security1.config.auth.provider.NaverUserInfo;
import com.course.security1.config.auth.provider.OAuth2UserInfo;
import com.course.security1.model.User;
import com.course.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // 구글 로그인 후 받은 userRequest 데이터에 대한 후처리 되는 함수 --> 강제 회원가입
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.

    // username = google_128391231231123 (google_${sub})
    // password = 암호화(겟인데어)
    // email = aaa@aaa.com
    // role = ROLE_USER
    // provider = gogole
    // providerId = 128391231231123
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getClientRegistration:{}", userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 하는지
        log.info("getTokenValue:{}", userRequest.getAccessToken().getTokenValue());


        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken 요청
        // userRequest 정보 -> loadUser 함수 -> 구글로부터 회원 프로필을 받아준다.
        log.info("getAttriutes:{}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google

        OAuth2UserInfo oAuth2UserInfo = null;

        if ("google".equals(provider)) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if ("facebook".equals(provider)) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if ("naver".equals(provider)) {
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else {
            log.error("지원하지 않는 provider:{}", provider);
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String encryptedPassword = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return new PrincipalDetails(userOptional.get(), oAuth2User.getAttributes());
        }

        User newUser = User.builder()
                .username(username)
                .password(encryptedPassword)
                .email(email)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();

        userRepository.save(newUser);

        return new PrincipalDetails(newUser, oAuth2User.getAttributes());
    }
}