package journeybuddy.spring.config.OAuth2;

import io.swagger.models.auth.OAuth2Definition;
import journeybuddy.spring.config.JWT.CustomUserDetails;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Setter
@Service
@Slf4j
@Getter
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // registrationId 카카오에서 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId: {}", registrationId);

        // userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        log.info("userNameAttributeName: " + userNameAttributeName);

        // 유저 정보 DTO 생성
        OAuth2UserAttribute oAuth2UserAttribute = OAuth2UserAttribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        log.info("getAttributes(): " + oAuth2User.getAttributes());

        // 회원가입 및 로그인
        User user = getOrSave(oAuth2UserAttribute);

        //principalDetails반환시
        //return new PrincipalDetails(user, oAuth2User.getAttributes());

        // DefaultOAuth2User 반환
        return new CustomUserDetails(
                user,
                oAuth2User.getAttributes(), // 사용자 속성
                userNameAttributeName // 사용자 이름 속성
        );
    }

    private User getOrSave(OAuth2UserAttribute oAuth2UserAttribute) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(oAuth2UserAttribute.getEmail())
                .orElseGet(() -> userRepository.save(oAuth2UserAttribute.toEntity()));
        return user;
    }

}


    /* 이건 내가 직접 access 토큰 받아서 구하는 방법
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final String kakaoUserInfoEndpoint = "https://kapi.kakao.com/v2/user/me"; // 카카오 사용자 정보 조회 URL
    private final String kakaoTokenHeader = "Bearer ";

    public OAuth2User loadUserByToken(String accessToken) {
        // 카카오 사용자 정보 조회
        String url = kakaoUserInfoEndpoint;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoTokenHeader + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        Map<String, Object> attributes = responseEntity.getBody();

        if (attributes == null) {
            throw new RuntimeException("Failed to fetch user details from Kakao");
        }

        // 카카오 사용자 정보 변환
        OAuth2UserAttribute oAuth2UserAttribute = OAuth2UserAttribute.of("kakao", "id", attributes);

        // 사용자 정보를 OAuth2User로 변환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2UserAttribute.toMap(),
                "id" // 사용자 이름 속성
        );
    }
}
*/





