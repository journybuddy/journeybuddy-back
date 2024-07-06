package journeybuddy.spring.service.UserService;


import jakarta.servlet.http.HttpSession;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static journeybuddy.spring.apiPayload.code.status.ErrorStatus.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger log = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User addUser(UserRequestDTO.UpdateDTO request) {
        User addUser = UserUpdateConverter.toUser(request,bCryptPasswordEncoder);
        if(!userRepository.existsByEmail(request.getEmail())){
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.setContext(context);
        return userRepository.save(addUser);
        }else{
            log.error("이미 존재하는 이메일입니다.");
            throw new RuntimeException();
        }
    }


    @Override
    public User updateUser(UserDetails userDetails,UserRequestDTO.UpdateDTO request) {
        Long userId = request.getId();

        if (!userRepository.existsById(userId)) {
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        if(!userRepository.existsByEmail(request.getEmail())){
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            String password = request.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = bCryptPasswordEncoder.encode(password);

            existingUser.setNickname(request.getNickname());
            existingUser.setEmail(request.getEmail());
            existingUser.setBio(request.getBio());
            existingUser.setPassword(hashedPassword);
            existingUser.setUpdatedAt(request.getUpdatedAt());

            return userRepository.save(existingUser);

        }else{
            log.error("이미 존재하는 이메일입니다.");
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }
    }

    @Override
    public User getUserById(Long id) {

        if (!userRepository.existsById(id)) {
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        return userRepository.findById(id).orElse(null);
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
    public Long loginCheck(UserRequestDTO.UpdateDTO request, HttpSession session) {
        User loginUser = userRepository.findById(request.getId()).orElse(null); 
        if (loginUser != null && loginUser.getPassword().equals(request.getPassword())) {
            session.setAttribute("userId", loginUser.getId());
            log.info("loginUser:" + loginUser.getId());
            return loginUser.getId();
        }
        return null;
    }

    @Override
    public void logout(HttpSession session) {
            session.invalidate();
    }

    @Override
    public boolean EmailDuplicationCheck(UserRequestDTO.UpdateDTO request) {
        return userRepository.findByEmail(request.getEmail()).isPresent();
    }

}
