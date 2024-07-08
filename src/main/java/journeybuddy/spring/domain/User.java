package journeybuddy.spring.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false,unique = true)
    private String email;

//    @Column(nullable = false)
    private String bio;

    @NotNull(message = "Password must not be null")
    private String password;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLike> userLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Plan> planList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vote> voteList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
    )

    private List<Role> roles;

    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("UserDetailsImpl -> getAuthorities : OK");

        if (roles.isEmpty()) {
            log.info("authorities is empty");
        }else {
            log.info("authorities size: {}", roles.size());
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
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
}
