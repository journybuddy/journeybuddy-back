package journeybuddy.spring.service.CommentService;

import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.web.dto.CommentDTO.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentCommandService {
    Page<CommentResponseDTO> checkMyComment(String userEmail,Pageable pageable);
    Comment commentSave(String userEmail,Long postId, Comment comment);

}
