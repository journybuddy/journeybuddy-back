package journeybuddy.spring.repository;

import journeybuddy.spring.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findPostsByUserId(Long userId); //userId를 통해서 게시글을 조회한다. optional아니고 List사용
 //   Optional<Post> findPostsByPostId(Long id); //postId를 통해서 게시글을 상세조회한다.
 //   Optional<Post> findById(Long id);
}
