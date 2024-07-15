package journeybuddy.spring.web.dto.PostDTO;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Integer listSize;
    private Integer totalPage;

}
