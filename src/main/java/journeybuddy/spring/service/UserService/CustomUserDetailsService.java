package journeybuddy.spring.service.UserService;

import journeybuddy.spring.domain.Role;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLSelect;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isEmpty()) {
            throw new UsernameNotFoundException("사용자 이메일을 입력해주세요.");
        }
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            log.info("사용자를 찾았습니다. Email: {}", email);

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),     // username
                    user.getPassword(),              // password
                    getAuthorities(user.getRoles())   // authorities (roles)
            );
        } catch (UsernameNotFoundException e) {
            log.error("사용자를 찾을 수 없습니다: " + email);
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보를 불러오는 도중 오류가 발생했습니다: " + email, e);
            throw new InternalAuthenticationServiceException("사용자 정보를 불러오는 도중 오류가 발생했습니다: " + email, e);
        }
    }


    private List<GrantedAuthority> getAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }


}
/*
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            log.info("사용자 정보를 찾았습니다. ID: {}", id);
            User user = userOptional.get();
            log.info("사용자 정보: {}", user);
            return user;
        } else {
            log.error("사용자 정보를 찾을 수 없습니다. ID: {}", id);
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }


    private List<SimpleGrantedAuthority> getRoles(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
*/
