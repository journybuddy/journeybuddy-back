package journeybuddy.spring.service.community.like;

import jakarta.transaction.Transactional;
import journeybuddy.spring.converter.community.UserLikeConverter;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.community.UserLike;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.community.UserLikeRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserLikeServiceImpl implements UserLikeService {

    private final UserLikeRepository userLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 좋아요 누르기
    @Override
    public UserLikeResponesDTO saveLikes(String userEmail, Long postId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<UserLike> userLike = userLikeRepository.findByUserAndPost(user, post);
        if (userLike.isPresent()) {
            log.error("이미 좋아요를 누른 게시물입니다.");
            throw new RuntimeException("이미 좋아요를 누른 게시물입니다.");
        }

        UserLike newUserLike = UserLike.builder()
                .user(user)
                .post(post)
                .build();


        post.setLikeCount(post.getLikeCount() + 1);
        userLikeRepository.save(newUserLike);

        return UserLikeConverter.toUserLikeResponesDTO(newUserLike);
    }


    @Override
    public void deleteLikes(String userEmail, Long postId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<UserLike> userLike = userLikeRepository.findByUserAndPost(user, post);
        if (userLike.isEmpty()) {
            log.error("좋아요를 누르지 않은 게시물입니다.");
            throw new RuntimeException("좋아요를 누르지 않은 게시물입니다.");
        }


        post.setLikeCount(post.getLikeCount() - 1);
        userLikeRepository.delete(userLike.get());
    }


    public Page<UserLikeResponesDTO> findMyLike(String userEmail,Pageable pageable){
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        Page<UserLike> likesByUser = userLikeRepository.findAllByUser(user, pageable);
        return likesByUser.map(UserLikeConverter::toUserLikeResponesDTO);


    }
}
