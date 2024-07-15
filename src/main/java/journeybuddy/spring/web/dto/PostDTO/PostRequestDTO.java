package journeybuddy.spring.web.dto.PostDTO;

import journeybuddy.spring.domain.Post;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostRequestDTO {
    private Long id;
    private String title;
    private String content;
    private Integer listSize;
    private Integer totalPage;


    // Post 객체를 PostRequestDTO로 변환하는 메서드
    /*
    public static PostRequestDTO toDTO(Post post){
        return PostRequestDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

  */
}
