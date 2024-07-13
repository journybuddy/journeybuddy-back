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

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!request.getEmail().equals(existingUser.getEmail())) {
            throw new RuntimeException("사용자 인증 정보가 일치하지 않습니다.");
        }

        /* 이메일 바꾸기 어떻게 할건지 정하기. 현재 수정기능은 이메일과 비밀번호 인증해야 가능함
        if (userRepository.existsByEmail(request.getEmail()) && !request.getEmail().equals(existingUser.getEmail())) {
            log.error("이미 존재하는 이메일");
            throw new TempHandler(ErrorStatus._BAD_REQUEST);
        }
        */

        existingUser.setNickname(request.getNickname());
        existingUser.setEmail(request.getEmail());
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
    public Long loginCheck(UserRequestDTO.UpdateDTO request) {
        User loginUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (loginUser != null &&  bCryptPasswordEncoder.matches(request.getPassword(), loginUser.getPassword())) {
            log.info("로그인한 유저:" + loginUser.getId());
            return loginUser.getId();
        }
        return null;
    }

    @Override
    public boolean EmailDuplicationCheck(UserRequestDTO.UpdateDTO request) {
        return userRepository.findByEmail(request.getEmail()).isPresent();
    }

    @Override
    public String getUserEmailById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return user.getEmail();
    }

    @Override
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }


}
