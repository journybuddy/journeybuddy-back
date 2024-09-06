package journeybuddy.spring.repository.community;

import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByUser(User user, Pageable pageable);

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);
}
