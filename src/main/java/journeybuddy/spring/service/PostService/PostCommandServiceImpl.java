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

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<Post> checkMyPost(String email) { //리스트타입 나중에 페이징처리할것
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("존재하지 않는 사용자");
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }else{
            return postRepository.findPostsByUserEmail(email);
        }
    }

    @Override
    public Post checkPostDetail(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.error("포스트가 존재하지 않습니다. postId: {}", postId);
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        return optionalPost.get();
    }

    @Override
    public Post savePost(String userEmail, Post post) { //userId가 외래키
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
        post.setUser(user);
        log.info("Saving post: {}", post);
        return postRepository.save(post);
    }

    @Override
    public Post deletePost(Long id) {
        if (id == null || !postRepository.existsById(id)) {
            log.error("존재하지 않는 포스트 입니다,Id:{}", id);
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        postRepository.deleteById(id);
        log.info("Deleted post: {}", id);
     return null;
    }


}
