package journeybuddy.spring.web.controller;

import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.config.JWT.SecurityUtil;
import journeybuddy.spring.config.JWT.SecurityUtils;
import journeybuddy.spring.converter.PostConverter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.PostService.PostCommandService;
import journeybuddy.spring.web.dto.PostDTO.PostRequestDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//게시글 조회기능 컨트롤러
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostRestController {
    private final PostRepository postRepository;
    private final PostCommandService postCommandService;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    //내가 쓴 모든 게시글 조회, 페이징 처리하기
    @GetMapping("/{userId}")
    public ApiResponse<List<Post>> checkMyPost(@PathVariable("userId") Long userId) {
        List<Post> posts = postCommandService.checkMyPost(userId);
        log.info("게시글 조회 userId = {}", userId);
        return ApiResponse.onSuccess(posts);
    }
    //게시글 저장

    @PostMapping("/save")
    public ApiResponse<PostRequestDTO> savePost(@RequestBody PostRequestDTO requestDTO,
                                                @RequestParam Long userId) {
        Post savedPost = PostConverter.toPost(requestDTO);
        User user = userRepository.findById(userId).orElse(null);
        Post savedPostSaved = postCommandService.savePost(user,savedPost);
        PostRequestDTO savedDTO = PostConverter.toPostRequestDTO(savedPostSaved);
        log.info("게시글 저장성공 userId = {}", userId);
        return ApiResponse.onSuccess(savedDTO);
    }

    //내가 쓴 게시글 상세조회(클릭시)
    @GetMapping("/postDetail")
    public ApiResponse<Post> checkMyPostDetail(@PathVariable("postId") Long postId) {
        Long currentUserId = securityUtil.getCurrentUserId(); // 현재 로그인한 사용자의 ID를 가져옴

        // postId를 이용해 포스트 상세 정보 조회
        Post detailPost = postCommandService.checkPostDetail(currentUserId, postId);

        if (detailPost == null) {
            return ApiResponse.onFailure("fail code", "없는 포스트", null);
        } else {
            return ApiResponse.onSuccess(detailPost);
        }
    }

}


    //게시글 삭제



