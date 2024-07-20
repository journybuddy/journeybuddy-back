package journeybuddy.spring.web.dto.UserLikeDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLikeRequestDTO {

 //   private Long userId;
    private Long postId;
    private Long id;

}
