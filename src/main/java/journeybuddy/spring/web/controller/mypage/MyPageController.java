package journeybuddy.spring.web.controller.mypage;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.community.PostConverter;
import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.repository.community.CommentRepository;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.service.community.comment.CommentService;
import journeybuddy.spring.service.community.like.UserLikeServiceImpl;
import journeybuddy.spring.service.community.post.PostCommandService;
import journeybuddy.spring.service.community.scrap.ScrapService;
import journeybuddy.spring.service.plan.info.PlanInfoService;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostListResponse;
import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import journeybuddy.spring.web.dto.plan.response.PlanListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my_page")
public class MyPageController {
    private final PostRepository postRepository;
    private final PostCommandService postCommandService;
    private final CommentService commentService;
    private final UserLikeServiceImpl userLikeServiceImpl;
    private final ScrapService scrapService;
    private final CommentRepository commentRepository;
    private final PlanInfoService planInfoService;

    @Operation(summary = "내가 누른 좋아요 확인", description = "내가 누른 좋아요 확인")
    @GetMapping("userlikes/myLikes")
    public ApiResponse<Page<UserLikeResponesDTO>> findMyLikes(@AuthenticationPrincipal UserDetails userDetails,
                                                              Pageable pageable) {
        String userEmail = userDetails.getUsername();
        Page<UserLikeResponesDTO> likesPage = userLikeServiceImpl.findMyLike(userEmail,pageable);
        return ApiResponse.onSuccess(likesPage);
    }

    @Operation(summary = "내가 스크랩한 게시글 리스트 확인", description = "내가 스크랩한 게시글 리스트 확인")
    @GetMapping("scraps/myScrap")
    public ApiResponse<Page<ScrapResponseDTO>> getMyScrap(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "9") int size) {

        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername();
        Page<ScrapResponseDTO> scrapPage = scrapService.findAll(userEmail,pageable);
        return ApiResponse.onSuccess(scrapPage);
    }


    @DeleteMapping("/posts/{postId}/delete")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
    public ApiResponse<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal  UserDetails userDetails) {
        if (postId != null) {
            postCommandService.deletePost(postId,userDetails.getUsername());
            log.info("Post with id {} deleted successfully", postId);
            return ApiResponse.onSuccess(null);
        } else {
            ApiResponse.onFailure("COMMON404", "사용자 정보가 없음", null);
        }
        return null;
    }


    @GetMapping("/my_posts/posts/my_posts")
    @Operation(summary = "내가 쓴 게시글 리스트 확인", description = "내가 쓴 게시글 리스트 확인")
    public ApiResponse<Page<PostListResponse>> getMyPostsPage(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "9") int size,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername();
        log.info("게시글 조회 userId = {}", userEmail);
        Page<PostListResponse> myPost = postCommandService.getMyPeed(userEmail,pageable);
        return ApiResponse.onSuccess(myPost);
    }



    @Operation(summary = "모든 포스트 게시글 페이징", description = "모든 포스트 게시글 페이징")
    @GetMapping("/checkAllPost")
    public ApiResponse<Page<PostResponseDTO>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "9") int size) {
        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAll(pageable);
        return ApiResponse.onSuccess(PostConverter.toDtoList(posts));
    }

    @GetMapping("/comment/myComment")
    @Operation(summary = "내가 쓴 댓글 확인", description = "내가 쓴 댓글 확인")
    public ApiResponse <Page<CommentResponseDTO>> checkMyComment(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "9") int size) {

        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername();
        log.info("나의 모든 댓글 조회");
        Page<CommentResponseDTO> checkMyComment = commentService.checkMyComment(userEmail,pageable);
        return ApiResponse.onSuccess(checkMyComment);
    }


    @GetMapping("/plans")
    @Operation(summary = "나의 계획 리스트 조회", description = "나의 계획 리스트 조회")
    public ResponseEntity<List<PlanListResponse>> getMyPlans(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(planInfoService.getPlanList(userDetails.getUsername(), pageable));
    }
}
