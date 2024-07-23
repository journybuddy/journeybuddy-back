package journeybuddy.spring.config.JWT;

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
    private Map<String, Object> attributes;
    private final String userNameAttributeName;

    // UserDetails 생성자
    public CustomUserDetails(User user) {
        this.user = user;
        this.attributes = Collections.emptyMap(); // 기본값으로 빈 맵
        this.userNameAttributeName = "id"; // 기본값
    }

    // OAuth2User 생성자
    public CustomUserDetails(User user, Map<String, Object> attributes, String userNameAttributeName) {
        this.user = user;
        this.attributes = attributes;
        this.userNameAttributeName = userNameAttributeName;
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
        // userNameAttributeName을 통해 사용자 이름을 가져옴
        return attributes.getOrDefault(userNameAttributeName, "").toString();
    }
}