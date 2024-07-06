package journeybuddy.spring.web.dto.UserDTO;

import journeybuddy.spring.domain.User;
import lombok.*;

import java.time.LocalDateTime;

public class UserResponseDTO {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateResultDTO {
        Long id;
        String nickname;
        String username;
        String email;
        String bio;
        String password;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

    }



}
