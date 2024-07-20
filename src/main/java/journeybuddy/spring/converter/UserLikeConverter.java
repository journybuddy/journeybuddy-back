package journeybuddy.spring.converter;

import journeybuddy.spring.domain.UserLike;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeRequestDTO;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeResponesDTO;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
@Builder
public class UserLikeConverter {

    public static UserLike toUserLike(UserLikeRequestDTO requestDTO) {
        return UserLike.builder()
                .id(requestDTO.getId())
                .build();
    }

    public  static UserLikeRequestDTO toUserLikeRequestDTO(UserLike userLike) {
        return UserLikeRequestDTO.builder()
        //        .userId(userLike.getUser().getId())
                .postId(userLike.getPost().getId())
                .build();
    }

    public  static UserLikeResponesDTO toUserLikeResponesDTO(UserLike userLike) {
        return UserLikeResponesDTO.builder()
                .userId(userLike.getUser().getId())
                .postId(userLike.getPost().getId())
                .id(userLike.getId())
                .build();
    }
}
