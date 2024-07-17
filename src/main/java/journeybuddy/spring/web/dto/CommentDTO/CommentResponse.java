package journeybuddy.spring.web.dto.CommentDTO;

import journeybuddy.spring.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class CommentResponse {
    private Long id;
    private String comment;
    private String userName; //알람 동작 시킨 user
    private Long postId;
    private String createdAt;
    private String lastModifiedAt;



    public static CommentResponse toResponse(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUser().getEmail(),
                comment.getPost().getId(),
                comment.getCreatedAt().toString(),
                comment.getUpdatedAt().toString()

        );
    }

}
