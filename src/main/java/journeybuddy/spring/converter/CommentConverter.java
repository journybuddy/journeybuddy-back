package journeybuddy.spring.converter;

import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.web.dto.CommentDTO.CommentRequestDTO;
import journeybuddy.spring.web.dto.CommentDTO.CommentResponseDTO;
import org.springframework.data.domain.Page;

public class CommentConverter {

    //toComment에 저장
    public static Comment toComment(CommentRequestDTO commentRequestDTO) {
        return Comment.builder()
                .id(commentRequestDTO.getId())
                .comment(commentRequestDTO.getComment())
                .build();

    }
    //comment에서 받아와서 toCommentRequestDTO로
    public static CommentRequestDTO toCommentRequestDTO(Comment comment) {
        return CommentRequestDTO.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .build();
    }

    //comment에서 받아와서 toCommentResponseDTO로
    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .build();
    }

    //paging 에 필요한 converter
    public static Page<CommentResponseDTO> toCommentDTOPage(Page<Comment> comments) {
        Page<CommentResponseDTO> CommentDTOList =
                comments.map(page->CommentResponseDTO.builder()
                        .id(page.getId())
                        .comment(page.getComment())
                        .userId(page.getUser().getId())
                        .postId(page.getPost().getId())
                        .build());
        return CommentDTOList;
    }
}
