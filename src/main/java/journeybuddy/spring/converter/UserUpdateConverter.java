package journeybuddy.spring.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



@RequiredArgsConstructor
@Slf4j
public class UserUpdateConverter{



    public static User toUser(UserRequestDTO.UpdateDTO request,BCryptPasswordEncoder bCryptPasswordEncoder) { //User엔티티에 저장
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