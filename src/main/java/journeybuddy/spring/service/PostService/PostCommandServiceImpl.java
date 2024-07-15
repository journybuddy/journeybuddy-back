package journeybuddy.spring.service.PostService;

import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;  //findpostbyUserId, findpostbyPostId
    private final UserRepository userRepository;

    @Override
    public List<Post> checkMyPost(Long userId) { //리스트타입 나중에 페이징처리할것
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return postRepository.findPostsByUserId(userId);
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Post checkPostDetail(Long userId,Long postId) {
        Long Id = userRepository.findById(userId).get().getId();
        if(Id!=null&&postId!=null){
            return postRepository.findById(postId).get();
        }
        return null;

    }

    @Override
    public Post savePost(User userId, Post post) { //userId가 외래키
        User user = userRepository.findById(userId.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        post.setUser(user);
        log.info("Saving post: {}", post);
        return postRepository.save(post);
    }

    @Override
    public Post deletePost(Long id) {
        if(postRepository.existsById(id)) {
            postRepository.deleteById(id);
            log.info("deleted post: {}", id);
        }
        return null;
    }
}
