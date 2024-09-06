package journeybuddy.spring.converter.community;

import journeybuddy.spring.domain.community.Image;
import journeybuddy.spring.domain.community.Scrap;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import journeybuddy.spring.web.dto.community.scrap.ScrapRequestDTO;
import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

import static com.amazonaws.services.ec2.model.ResourceType.Image;

@Builder
public class ScrapConverter {

    public static Scrap toScrap(ScrapRequestDTO scrapRequestDTO){
        return Scrap.builder()

                .build();
    }

    public static ScrapRequestDTO toScrapRequesetDTO(Scrap scrap) {
        return ScrapRequestDTO.builder()
                .postId(scrap.getPost().getId())
                .build();
    }


    public static ScrapResponseDTO toScrapResponseDTO(Scrap scrap) {
        return ScrapResponseDTO.builder()
                .id(scrap.getId())
                .userId(scrap.getUser() != null ? scrap.getUser().getId() : null)
                .postResponse(PostResponse.builder()
                        .postId(scrap.getPost().getId())
                        .title(scrap.getPost().getTitle())
                        .content(scrap.getPost().getContent())
                        .location(scrap.getPost().getLocation())
                        .userId(scrap.getPost().getUser().getId())
                        .userName(scrap.getPost().getUser().getNickname())
                        .imageUrlList(scrap.getPost().getImages().stream()
                                .map(journeybuddy.spring.domain.community.Image::getUrl)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }


    public static Page<ScrapResponseDTO> toScrapPage(Page<Scrap> scrap){
        return scrap.map(ScrapConverter::toScrapResponseDTO);
    }
}
