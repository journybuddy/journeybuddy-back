package journeybuddy.spring.web.dto.CommentDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class CommentResponseDTO {
    private Long id;
    private String comment;
    private Long userId; //알람 동작 시킨 user
    private Long postId;
    private String createdAt;
    private String updatedAt;
}
