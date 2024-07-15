package journeybuddy.spring.config.JWT;

import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.UserService.CustomUserDetails;
import journeybuddy.spring.service.UserService.UserCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserCommandService userCommandService;

    public void authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String getUserEmailById(Long userId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();
        return userCommandService.getUserEmailById(userId);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentId = Long.valueOf(authentication.getName()); // 현재 로그인한 사용자의 ID를 가져옴
        return currentId;
    }

}
