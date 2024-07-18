package journeybuddy.spring.repository;

import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //내가 쓴 댓글찾기
    Page<Comment> findAllByUser(User user, Pageable pageable);

}
