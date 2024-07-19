package journeybuddy.spring.web.dto.ScrapDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapResponseDTO {
    private Long id;
    private Long postId;
    private Long userId;
}
