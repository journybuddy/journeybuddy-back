package journeybuddy.spring.web.dto.PostDTO;


import journeybuddy.spring.domain.User;
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
    private Integer currentPage;
    private Integer totalPages;
    private Long userId;

}
