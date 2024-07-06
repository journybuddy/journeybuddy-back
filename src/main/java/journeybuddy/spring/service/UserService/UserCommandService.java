package journeybuddy.spring.service.UserService;

import jakarta.servlet.http.HttpSession;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserCommandService {
    User updateUser(UserDetails userDetails,UserRequestDTO.UpdateDTO request);
    User addUser(UserRequestDTO.UpdateDTO request);
    User getUserById(Long id);
    User deletedById(Long id);
    public Long loginCheck(UserRequestDTO.UpdateDTO request, HttpSession session);
    public void logout(HttpSession session);
    public boolean EmailDuplicationCheck(UserRequestDTO.UpdateDTO request);
}
