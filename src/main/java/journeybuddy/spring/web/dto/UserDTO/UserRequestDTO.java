package journeybuddy.spring.web.dto.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;


public class UserRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateDTO extends BaseEntity{
//        Long id;
        String bio;
        String email;
        String nickname;
        String password;
//        LocalDateTime createdAt;
//        LocalDateTime updatedAt;


    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginDTO {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }



}
