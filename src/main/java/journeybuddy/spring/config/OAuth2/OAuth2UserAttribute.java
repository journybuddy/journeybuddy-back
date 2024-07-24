package journeybuddy.spring.config.OAuth2;

import journeybuddy.spring.domain.Role;
import journeybuddy.spring.domain.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class OAuth2UserAttribute {

    public String nickname;
    public String email;
    private Map<String, Object> attributes;

    public static OAuth2UserAttribute of(String registrationId, Map<String, Object> attributes) {
        log.info("RegistrationId: {}", registrationId);
        log.info("Attributes: {}", attributes);

        if ("kakao".equals(registrationId)) {
            return ofKaKao(attributes);
        }
        throw new IllegalArgumentException("Unknown registrationId: " + registrationId);
    }

    //카카오로 로그인
    public static OAuth2UserAttribute ofKaKao(Map<String, Object> attributes) {
        log.info("Processing Kakao attributes: {}", attributes);

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) account.get("email");
        Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");
        String nickname = (String) profile.get("nickname");

        log.info("Kakao Email: {}", email);
        log.info("Kakao Nickname: {}", nickname);


        return OAuth2UserAttribute.builder()
                .nickname(nickname)
                .email(email)
                .attributes(account)
                .build();
    }

    //user 객체로 변환한다
    public User toEntity() {
        log.info("Converting OAuth2UserAttribute to User entity: Nickname={}, Email={}", nickname, email);
        return User.builder()
                .nickname(nickname)
                .email(email)
                .build();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", nickname);
        map.put("email", email);
        log.info("Converted OAuth2UserAttribute to Map: {}", map);
        return map;
    }
}