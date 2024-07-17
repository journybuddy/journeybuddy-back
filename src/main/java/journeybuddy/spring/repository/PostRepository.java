package journeybuddy.spring.repository;

import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    List<Post> findPostsByUserEmail(String email);
//    Page<Post> findAll(Pageable pageable);
    Page<Post> findAllByUser(User user, Pageable pageable);

}
