package journeybuddy.spring.service.KaKaoService;
/*
import jakarta.servlet.http.HttpServletResponse;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.config.OAuth2.CustomOAuth2UserService;
import journeybuddy.spring.config.OAuth2.OAuth2UserAttribute;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KaKaoCommandServiceImpl {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Transactional
    public String loginByKakao(String accessToken) {
        // 액세스 토큰을 사용하여 카카오 사용자 정보 조회
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        // 사용자 이메일을 사용하여 사용자 등록 또는 업데이트
        String email = (String) userInfo.get("email");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .nickname((String) userInfo.get("nickname"))
                            .build();
                    return userRepository.save(newUser);
                });

        // JWT 생성
        return jwtUtil.generateToken(Collections.emptyMap(), user);
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }

    @Transactional
    public User signupByKakao(String accessToken) {
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
        String email = (String) userInfo.get("email");

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .nickname((String) userInfo.get("nickname"))
                            .build();
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public User updateKaKaoUser(String accessToken, String newNickname) {
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
        String email = (String) userInfo.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setNickname(newNickname);
        return userRepository.save(user);
    }
}

/*  accessToken직접 조회

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Transactional
    public String loginByKakao(String accessToken) {
        // 액세스 토큰을 사용하여 카카오 사용자 정보 조회
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        // 사용자 이메일을 사용하여 사용자 등록 또는 업데이트
        String email = (String) userInfo.get("email");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .nickname((String) userInfo.get("nickname"))
                            .build();
                    return userRepository.save(newUser);
                });

        // JWT 생성
        return jwtUtil.generateToken(Collections.emptyMap(), user);
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }

    @Transactional
    public User signupByKakao(String accessToken) {
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
        String email = (String) userInfo.get("email");

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .nickname((String) userInfo.get("nickname"))
                            .build();
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public User updateKaKaoUser(String accessToken, String newNickname) {
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
        String email = (String) userInfo.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setNickname(newNickname);
        return userRepository.save(user);
    }
}
 */