package journeybuddy.spring.converter.community;
import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.domain.community.Image;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.*;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public class PostConverter {

    private static ImageDTO toImageDTO(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }


    //게시글 상세보기
    public static PostDetailResponse toPostDetailResponse(Post post, Page<Comment> commentPage) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .writer(post.getUser().getNickname())
                .createdAt(post.getCreatedAt().toString())
                .imageUrlList(post.getImages().stream()
                        .map(PostConverter::toImageDTO)
                        .collect(Collectors.toList()))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .commentList(commentPage.getContent().stream()
                        .map(comment -> CommentDTO.builder()
                                .commentId(comment.getCommentId())
                                .content(comment.getComment())
                                .userId(comment.getUser().getId())
                                .nickname(comment.getUser().getNickname())
                                .createdAt(comment.getCreatedAt().toString())
                                .build())
                        .collect(Collectors.toList()))
                .totalPages(commentPage.getTotalPages())
                .totalComments(commentPage.getTotalElements())
                .build();
    }

    //게시글 목록보기
    public static PostListResponse toPostListResponse(Post post) {
        return PostListResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerName(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .location(post.getLocation())
                .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getUrl())
                .build();
    }

    public static PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .userId(post.getUser().getId())
                .userName(post.getUser().getNickname())
                .imageUrlList(post.getImages().stream()
                        .map(Image::getUrl)
                        .collect(Collectors.toList()))
                .build();
    }


        public static PostResponseDTO toPostResponseDTO(Post post) {  //post에서 받아옴 PostResponseDTO로
            return PostResponseDTO.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .title(post.getTitle())
                    .userId(post.getUser().getId())
                    .build();
        }

        /* Page<Entity> -> Page<Dto> 변환처리 */
        public static Page<PostResponseDTO> toDtoList(Page<Post> posts) {
            return posts.map(post -> {
                //null확인안하면 exception발생
                Long userId = (post.getUser() != null) ? post.getUser().getId() : null;

                return PostResponseDTO.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .userId(userId)
                        .build();
            });
        }

    }