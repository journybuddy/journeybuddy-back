package journeybuddy.spring.config.JWT;

import journeybuddy.spring.config.OAuth2.OAuth2UserAttribute;
import journeybuddy.spring.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

//UserDetails는 사용자의 정보를 제공하는 인터페이스
@Slf4j
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    // UserDetails 생성자
    public CustomUserDetails(User user) {
        this.user = user;
        this.attributes = null;
    }

    // OAuth2User 생성자
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String getName() {
        // attributes에서 'nickname'을 반환
        return attributes != null ? (String) ((Map<String, Object>) attributes.get("profile")).get("nickname") : null;
    }

    public String getEmail() {
        // attributes에서 'email'을 반환
        return attributes != null ? (String) ((Map<String, Object>) attributes.get("kakao_account")).get("email") : null;
    }

}