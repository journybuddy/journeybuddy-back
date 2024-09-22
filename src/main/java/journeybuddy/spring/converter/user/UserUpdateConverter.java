package journeybuddy.spring.converter.user;

import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.web.dto.user.UserRequestDTO;
import journeybuddy.spring.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@RequiredArgsConstructor
@Slf4j
public class UserUpdateConverter{


    public static User toUserByUpdate(UserRequestDTO.UpdateDTO request,BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .bio(request.getBio())
                .build();

    }


    public static User toUserPassword(UserRequestDTO.PasswordDTO request,BCryptPasswordEncoder bCryptPasswordEncoder) {
        String encodePassword = bCryptPasswordEncoder.encode(request.getPassword());
        return User.builder()
                .password(encodePassword)
                .build();
    }


    public static User toUser(UserRequestDTO.RegisterDTO request,BCryptPasswordEncoder bCryptPasswordEncoder) { //User엔티티에 저장
        String encodePassword = bCryptPasswordEncoder.encode(request.getPassword());
        return User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .bio(request.getBio())
                .password(encodePassword)
                .build();

    }

    public static UserResponseDTO.UpdateResultDTO toUpdateResultDTO(User user){ //User객체 받아서 반환
        return UserResponseDTO.UpdateResultDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}