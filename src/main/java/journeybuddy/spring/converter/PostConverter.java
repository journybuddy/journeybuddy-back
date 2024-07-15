package journeybuddy.spring.converter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.web.dto.PostDTO.PostRequestDTO;

public class PostConverter {


        public static Post toPost(PostRequestDTO postRequestDTO) {  //Post 엔티티에 저장
            return Post.builder()
                    .id(postRequestDTO.getId())
                    .title(postRequestDTO.getTitle())
                    .content(postRequestDTO.getContent())
                    .build();

        }

    public static PostRequestDTO toPostRequestDTO(Post post) {  //post에서 받아옴
        return PostRequestDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();

    }
}