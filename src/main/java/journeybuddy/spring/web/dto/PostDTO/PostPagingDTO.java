package journeybuddy.spring.web.dto.PostDTO;

import lombok.Data;

@Data
public class PostPagingDTO {
    private int page;
    private int size;
    private String sort;
}
