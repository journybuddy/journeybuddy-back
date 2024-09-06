package journeybuddy.spring.converter.community;

import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.web.dto.community.comment.CommentRequestDTO;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public class CommentConverter {


    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getCommentId())
                .comment(comment.getComment())
                .userId(comment.getUser().getId())
                .postResponse(PostResponse.builder()
                        .postId(comment.getPost().getId())
                        .title(comment.getPost().getTitle())
                        .content(comment.getPost().getContent())
                        .location(comment.getPost().getLocation())
                        .userId(comment.getPost().getUser().getId())
                        .userName(comment.getPost().getUser().getNickname())
                        .imageUrlList(comment.getPost().getImages().stream()
                                .map(journeybuddy.spring.domain.community.Image::getUrl)
                                .collect(Collectors.toList()))
                        .build())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }


    public static Page<CommentResponseDTO> toCommentDTOPage(Page<Comment> comments) {
        Page<CommentResponseDTO> CommentDTOList =
                comments.map(page->CommentResponseDTO.builder()
                        .id(page.getCommentId())
                        .comment(page.getComment())
                        .userId(page.getUser().getId())
                        .build());
        return CommentDTOList;
    }
}
