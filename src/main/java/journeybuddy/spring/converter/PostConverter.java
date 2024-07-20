package journeybuddy.spring.converter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.web.dto.PostDTO.PostRequestDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public class PostConverter {


        public static Post toPost(PostRequestDTO postRequestDTO) {  //Post 엔티티에 저장
            return Post.builder()
                    .id(postRequestDTO.getId())
                    .title(postRequestDTO.getTitle())
                    .content(postRequestDTO.getContent())
                    .build();

        }

    public static PostRequestDTO toPostRequestDTO(Post post) {  //post에서 받아옴 PostRequestDTO로
        return PostRequestDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
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


    public static PostResponseDTO fromEntity(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
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