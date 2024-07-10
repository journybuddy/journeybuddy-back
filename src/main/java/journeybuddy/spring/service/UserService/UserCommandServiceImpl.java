package journeybuddy.spring.service.UserService;


import jakarta.servlet.http.HttpSession;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.Role;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.RoleRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static journeybuddy.spring.apiPayload.code.status.ErrorStatus.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger log = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Override
    public User addUser(UserRequestDTO.UpdateDTO request) {

        User addUser = UserUpdateConverter.toUser(request,bCryptPasswordEncoder);

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        addUser.setRoles(Collections.singletonList(defaultRole));
        if(!userRepository.existsByEmail(request.getEmail())){


            return userRepository.save(addUser);

        }else{
            log.error("이미 존재하는 이메일입니다.");
            throw new RuntimeException();
        }
    }


    @Override
    public User updateUser(UserRequestDTO.UpdateDTO request,String email) {
        Long userId = request.getId();

        // 사용자 존재 여부 확인
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 요청한 이메일과 기존 사용자의 이메일이 일치하는지 확인
        if (!request.getEmail().equals(existingUser.getEmail())) {
            throw new RuntimeException("사용자 인증 정보가 일치하지 않습니다.");
        }

        // 이미 존재하는 이메일인지 확인
        if (userRepository.existsByEmail(request.getEmail()) && !request.getEmail().equals(existingUser.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 기타 필드 업데이트
        existingUser.setNickname(request.getNickname());
        existingUser.setEmail(request.getEmail());
        existingUser.setBio(request.getBio());
        existingUser.setUpdatedAt(request.getUpdatedAt());

        // 사용자 저장 및 반환
        return userRepository.save(existingUser);
    }

    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public User deletedById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new TempHandler(MEMBER_NOT_FOUND);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id: " + id);
        return null;
    }

    @Override
    public Long loginCheck(UserRequestDTO.UpdateDTO request) {
        User loginUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (loginUser != null &&  bCryptPasswordEncoder.matches(request.getPassword(), loginUser.getPassword())) {

            log.info("loginUser:" + loginUser.getId());
            return loginUser.getId();
        }
        return null;
    }


    @Override
    public boolean EmailDuplicationCheck(UserRequestDTO.UpdateDTO request) {
        return userRepository.findByEmail(request.getEmail()).isPresent();
    }

    @Override
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }

}
