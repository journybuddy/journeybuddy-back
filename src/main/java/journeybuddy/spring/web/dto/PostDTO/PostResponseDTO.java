package journeybuddy.spring.web.dto.PostDTO;


import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

}
