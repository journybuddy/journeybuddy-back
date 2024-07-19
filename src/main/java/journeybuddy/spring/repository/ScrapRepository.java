package journeybuddy.spring.repository;

import journeybuddy.spring.domain.User;
import journeybuddy.spring.domain.mapping.Scrap;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Page<Scrap> findAllByUser(User user, Pageable pageable);
}
