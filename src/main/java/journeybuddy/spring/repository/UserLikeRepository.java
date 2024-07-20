package journeybuddy.spring.repository;

import journeybuddy.spring.domain.User;
import journeybuddy.spring.domain.UserLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    Page<UserLike> findAllByUser(User user, Pageable pageable);

}
