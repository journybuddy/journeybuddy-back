package journeybuddy.spring.service.UserService;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.Role;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.RoleRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public User addUser(UserRequestDTO.RegisterDTO request) {

        User addUser = UserUpdateConverter.toUser(request, bCryptPasswordEncoder);

        // 기본 역할을 가져오기
        Role defaultRole = roleRepository.findByName("USER")
                .orElse(null); // 기본 역할이 없을 경우 null 반환

        // 기본 역할이 존재하면 사용자에게 설정, 없으면 빈 역할 목록 설정
        if (defaultRole != null) {
            addUser.setRoles(Collections.singletonList(defaultRole));
        } else {
            addUser.setRoles(Collections.emptyList()); // 역할 없이도 사용자 추가 가능
        }

        // 이메일이 이미 존재하는지 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("이미 존재하는 이메일입니다.");
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 사용자 저장
        return userRepository.save(addUser);
    }


    @Override
    public User updateUser(UserRequestDTO.UpdateDTO request,String userEmail) {

        User existingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));


        existingUser.setNickname(request.getNickname());
        existingUser.setBio(request.getBio());
        existingUser.setUpdatedAt(request.getUpdatedAt());

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
    public Long loginCheck(UserRequestDTO.RegisterDTO request) {
        User loginUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (loginUser != null &&  bCryptPasswordEncoder.matches(request.getPassword(), loginUser.getPassword())) {
            log.info("로그인한 유저:" + loginUser.getId());
            return loginUser.getId();
        }
        return null;
    }

    @Override
    public boolean EmailDuplicationCheck(UserRequestDTO.RegisterDTO request) {
        return userRepository.findByEmail(request.getEmail()).isPresent();
    }

    @Override
    public String getUserEmailById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return user.getEmail();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }


}
