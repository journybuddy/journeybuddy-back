package journeybuddy.spring.converter;

import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.mapping.Scrap;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapRequestDTO;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapResponseDTO;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public class ScrapConverter {

    public static Scrap toScrap(ScrapRequestDTO scrapRequestDTO){
        return Scrap.builder()
                .id(scrapRequestDTO.getId())
                .build();
    }

    public static ScrapRequestDTO toScrapRequesetDTO(Scrap scrap) {
        return ScrapRequestDTO.builder()
                .id(scrap.getId())
                .postId(scrap.getPost().getId())
                .userId(scrap.getUser().getId())
                .build();
    }

    //nullcheck안하면 nullpointexception터짐
    public static ScrapResponseDTO toScrapResponseDTO(Scrap scrap) {
        return ScrapResponseDTO.builder()
                .id(scrap.getId())
                .postId(scrap.getPost() != null ? scrap.getPost().getId() : null)
                .userId(scrap.getUser() != null ? scrap.getUser().getId() : null)
                .build();
    }

    //페이지로 넘겨줄것들
    public static Page<ScrapResponseDTO> toScrapPage(Page<Scrap> scrap){
        return scrap.map(ScrapConverter::toScrapResponseDTO);
    }
}
