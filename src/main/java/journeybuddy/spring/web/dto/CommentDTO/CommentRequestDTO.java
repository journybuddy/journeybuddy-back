package journeybuddy.spring.web.dto.CommentDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class CommentRequestDTO {
    private Long id; //commentId
    private String comment; //내용
    private Long userId; //댓글단 사용자
}
