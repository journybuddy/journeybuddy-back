package journeybuddy.spring.service.community.post;

import journeybuddy.spring.converter.community.PostConverter;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.PostListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return postRepository.findPostsByUserEmail(email);
        }
    }

    @Override
    public Post checkPostDetail(Long postId, String authentication) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.error("포스트가 존재하지 않습니다. postId: {}", postId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Post post = optionalPost.get();
        if (!post.getUser().getEmail().equals(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 접근할 수 있습니다.");
        }
        return optionalPost.get();
    }


    @Override
    public Post deletePost(Long postId,String authentication) {
        Optional<Post> posts = postRepository.findById(postId);
        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Post post = posts.get();
        if (!post.getUser().getEmail().equals(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 접근할 수 있습니다.");
        }
        postRepository.deleteById(postId);
        log.info("Deleted post: {}", postId);
        return null;
    }

    @Override
    public List<PostListResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        List<PostListResponse> postResponseDTOS = posts.stream()
                .map(PostConverter::toPostListResponse)
                .collect(Collectors.toList());
        return postResponseDTOS;
    }


    public Page<Post> pageList(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getMyPeed(String userName, Pageable pageable) {
        User user = userRepository.findByEmail(userName).orElseThrow(()->{
            return new UsernameNotFoundException("User not found with email: " + userName);
        });

        Page<Post> postsByUser = postRepository.findAllByUser(user, pageable);
        return postsByUser.map(PostConverter::toPostListResponse);

    }

    private User checkUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        return user;
    }

}



