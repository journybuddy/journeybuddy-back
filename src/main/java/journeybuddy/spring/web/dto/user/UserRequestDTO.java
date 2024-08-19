package journeybuddy.spring.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


public class UserRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateDTO {
        String bio;
        String profile_image;
//        String email;
//        String nickname;
//        String password;
//        LocalDateTime updatedAt;


    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterDTO{
 //       Long id;
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PasswordDTO {

        @NotBlank
        private String password;
    }



}
