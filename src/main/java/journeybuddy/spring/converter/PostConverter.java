package journeybuddy.spring.converter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.web.dto.PostDTO.PostRequestDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;

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
                .content(post.getContent())
                .userId(post.getUser().getId())
                .build();

    }
}