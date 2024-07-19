package journeybuddy.spring.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.OAuth2Definition;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.config.JWT.SecurityUtil;
import journeybuddy.spring.config.JWT.SecurityUtils;
import journeybuddy.spring.converter.PostConverter;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.PostService.PostCommandService;
import journeybuddy.spring.web.dto.PostDTO.PostRequestDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//게시글 조회기능 컨트롤러
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRestController {

    private final PostCommandService postCommandService;
    private final PostRepository postRepository;

    //게시글 저장
    @PostMapping("/save")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PostRequestDTO> savePost(@RequestBody PostRequestDTO requestDTO,
                                                Authentication authentication) {
        Post savedPost = PostConverter.toPost(requestDTO);
        String userEmail = authentication.getName();
        Post savedPostSaved = postCommandService.savePost(userEmail, savedPost);
        PostRequestDTO savedDTO = PostConverter.toPostRequestDTO(savedPostSaved);
        log.info("게시글 저장 성공 userId = {}", userEmail);
        return ApiResponse.onSuccess(savedDTO);
    }

    //내가 쓴 게시글 상세조회(클릭시)
    @GetMapping("/mypost")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> checkMyPostDetail(@RequestParam Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        if (postId != null) {
            Post detailPost = postCommandService.checkPostDetail(postId, userDetails.getUsername());
            PostResponseDTO detailDTO = PostConverter.toPostResponseDTO(detailPost);
            return ApiResponse.onSuccess(detailDTO);
        } else {
            log.error("없는포스트");
            return ApiResponse.onFailure("COMMON404", "존재하지 않는포스트.", null);
        }
    }

    //게시글 삭제
    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal  UserDetails userDetails) {
        if (postId != null) {
            postCommandService.deletePost(postId,userDetails.getUsername());
            log.info("Post with id {} deleted successfully", postId);
            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.onFailure("COMMON404", "사용자 정보가 없음", null));
        }
    }

    //페이징 처리 안되어있음
    @GetMapping("/my_posts")
    @ApiOperation("내가 쓴 게시물 리스트 확인")
    public ApiResponse<List<PostResponseDTO>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername(); // JWT로 인증된 사용자의 이메일 가져오기
        List<Post> posts = postCommandService.checkMyPost(userEmail);
        List<PostResponseDTO> postResponseDTOS = posts.stream()
                .map(PostConverter::toPostResponseDTO)
                .collect(Collectors.toList());
        log.info("게시글 조회 userId = {}", userEmail);
        return ApiResponse.onSuccess(postResponseDTOS);
    }

    //페이징 처리 되어있음
    @GetMapping("/my_posts/paging")
    @ApiOperation("내가 쓴 게시물 리스트 확인")
    public ApiResponse<Page<PostResponseDTO>> getMyPostsPage(@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername(); // JWT로 인증된 사용자의 이메일 가져오기
        log.info("게시글 조회 userId = {}", userEmail);
        Page<PostResponseDTO> myPost = postCommandService.getMyPeed(userEmail,pageable);
        return ApiResponse.onSuccess(myPost);
    }


}









