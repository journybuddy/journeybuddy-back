package journeybuddy.spring.service.PostService;

import jdk.jshell.spi.ExecutionControl;
import journeybuddy.spring.converter.PostConverter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.PostDTO.PostPagingDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            log.error("존재하지 않는 사용자");
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
        return optionalPost.get();
    }

    @Override
    public Post savePost(String userEmail, Post post) { //userId가 외래키
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
        post.setUser(user);
        log.info("Saving post: {}", post);
        return postRepository.save(post);
    }

    @Override
    public Post deletePost(Long id) {
        if (id == null || !postRepository.existsById(id)) {
            log.error("존재하지 않는 포스트 입니다,Id:{}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        postRepository.deleteById(id);
        log.info("Deleted post: {}", id);
        return null;
    }

    @Override
    public List<PostResponseDTO> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        //stream을 이용해서 엔티티를 응답객체로 변경
        List<PostResponseDTO> postResponseDTOS = posts.stream()
                .map(PostConverter::toPostResponseDTO)
                .collect(Collectors.toList());
        return postResponseDTOS;
    }


    public Page<Post> pageList(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getMyPeed(String userName, Pageable pageable) {
        User user = checkUser(userName);
        Page<Post> postsByUser = postRepository.findAllByUser(user, pageable);
        //아래의 map()의 과정은 Page<Post> => Page<PostMineDto> 로 변환과정
        return postsByUser.map(PostConverter::toPostResponseDTO);

    }

    private User checkUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        return user;
    }
    //////////////////////////////////////////////////////////////////
    public Page<PostResponseDTO> findAllPost(PostPagingDTO postPagingDTO){
        Sort sort = Sort.by(Sort.Direction.fromString(postPagingDTO.getSort()), "id");
        Pageable pageable = PageRequest.of(postPagingDTO.getPage(), postPagingDTO.getSize(), sort);

        Page<Post> postPage = postRepository.findAll(pageable);
        Page<PostResponseDTO> postResponseDTOS = PostConverter.toDtoList(postPage);
        return postResponseDTOS;
    }
}



