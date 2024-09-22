package journeybuddy.spring.repository.plan;


import journeybuddy.spring.domain.plan.Random;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomURLRepository extends JpaRepository<Random, Integer> {

}