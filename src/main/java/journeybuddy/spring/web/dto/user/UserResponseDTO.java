package journeybuddy.spring.web.dto.user;

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
        String profile_img;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }
}
