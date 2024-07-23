package journeybuddy.spring.config.OAuth2;

import journeybuddy.spring.domain.Role;
import journeybuddy.spring.domain.User;
import lombok.*;
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
public class OAuth2UserAttribute {

    public String nickname;
    public String email;
//    private String nameAttributeKey;
    //    String profile;

    public static OAuth2UserAttribute of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKaKao(attributes);
        }
        throw new IllegalArgumentException("Unknown registrationId: " + registrationId);
    }

        public static OAuth2UserAttribute ofKaKao(Map<String, Object> attributes) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) attributes.get("kakao_profile");
            String nickname = (String) profile.get("nickname");
            String email = (String) account.get("email");



            return OAuth2UserAttribute.builder()
                    .nickname(nickname)
                    .email(email)
            //        .nameAttributeKey(userNameAttributeName);
                    .build();
        }

        public User toEntity() {
            return User.builder()
                    .nickname(nickname)
                    .email(email)
                    .build();
        }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", nickname);
        map.put("email", email);
        return map;
    }
    }