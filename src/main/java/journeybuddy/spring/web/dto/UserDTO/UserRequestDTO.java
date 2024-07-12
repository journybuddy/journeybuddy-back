package journeybuddy.spring.web.dto.UserDTO;


import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

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
