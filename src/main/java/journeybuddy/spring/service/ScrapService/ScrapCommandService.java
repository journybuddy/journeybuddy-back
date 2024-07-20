package journeybuddy.spring.service.ScrapService;

import journeybuddy.spring.domain.mapping.Scrap;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ScrapCommandService {
    Page<ScrapResponseDTO> findAll(String userEmail, Pageable pageable);
    Scrap saveScrap(String userEmail, Long postId, Scrap scrap);
}
