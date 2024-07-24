package journeybuddy.spring.service.UserLikeService;

import journeybuddy.spring.converter.UserLikeConverter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.domain.UserLike;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.UserLikeRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeResponesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLikeCommandServiceImpl implements UserLikeCommandService {

    private final UserLikeRepository userLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //좋아요 누르고 저장
    public UserLike saveLikes(String userEmail,Long postId, UserLike userLike) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        userLike.setUser(user);
        userLike.setPost(post);
        userLikeRepository.save(userLike);
        log.info("Saving UserLike: {}", userLike);
        return userLike;
    }

    //내가 누른 좋아요 확인
    public Page<UserLikeResponesDTO> findMyLike(String userEmail,Pageable pageable){
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        Page<UserLike> likesByUser = userLikeRepository.findAllByUser(user, pageable);
        return likesByUser.map(UserLikeConverter::toUserLikeResponesDTO);


    }
}
