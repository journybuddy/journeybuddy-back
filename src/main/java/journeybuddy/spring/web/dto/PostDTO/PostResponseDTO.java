package journeybuddy.spring.web.dto.PostDTO;


import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostResponseDTO {
/*    private Long id;
    private String title;
    private String content;
    private Long userId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();

        if (post.getUser() != null) {
            this.userId = post.getUser().getId();
        } else {
            this.userId = null; // 사용자 정보가 없는 경우
        }
    }

    public static Page<PostResponseDTO> toDtoList(Page<Post> postEntities){
        Page<PostResponseDTO> postDtoList = postEntities.map(m -> PostResponseDTO.builder()
                .id(m.getId())
                .title(m.getTitle())
                .userId(m.getUser().getId())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build());
        return postDtoList;
    }


    public static PostResponseDTO of(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
*/
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;


    public static PostResponseDTO fromEntity(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    /* Page<Entity> -> Page<Dto> 변환처리 */
    public static Page<PostResponseDTO> toDtoList(Page<Post> post){
        Page<PostResponseDTO> postDtoList =
                post.map(m -> PostResponseDTO.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .content(m.getContent())
                        .userId(m.getUser().getId())
                        .createdAt(m.getCreatedAt())
                        .build());

        return postDtoList;
    }


}
