package journeybuddy.spring.web.controller;

import io.swagger.annotations.Api;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    //내가 쓴 모든 게시글 조회, 페이징 처리하기
    //stream함수 매우 중요! 꼭 다시보기
    @GetMapping("/{userEmail}")
    public ApiResponse<List<PostResponseDTO>> checkMyPost(@PathVariable("userEmail") String userEmail) {
        List<Post> posts = postCommandService.checkMyPost(userEmail);
        List<PostResponseDTO> postResponseDTOS = posts.stream()
                        .map(PostConverter::toPostResponseDTO)
                                .collect(Collectors.toList());
        log.info("게시글 조회 userId = {}", userEmail);
        return ApiResponse.onSuccess(postResponseDTOS);
    }

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
    @GetMapping("/postDetail/{postId}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> checkMyPostDetail(@PathVariable("postId") Long postId) {
        try {
            Post detailPost = postCommandService.checkPostDetail(postId);
            PostResponseDTO detailDTO = PostConverter.toPostResponseDTO(detailPost);
            return ResponseEntity.ok(ApiResponse.onSuccess(detailDTO));
        } catch (TempHandler e) {
            log.error("포스트 조회 중 에러 발생: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.onFailure("COMMON404", "존재하지 않는포스트.", null));
        }
    }

    //게시글 삭제
    @DeleteMapping("/delete/{postId}")
 //   @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {
        try {
            postCommandService.deletePost(postId);
            log.info("Post with id {} deleted successfully", postId);
            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } catch (TempHandler e) {
            log.error("Failed to delete post with id {}: {}", postId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.onFailure("COMMON404", "사용자 정보가 없음", null));
        } catch (Exception e) {
            log.error("Error deleting post with id {}: ", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.onFailure("COMMON500", "서버 에러, 관리자에게 문의 바랍니다.", null));
        }

    }
}



