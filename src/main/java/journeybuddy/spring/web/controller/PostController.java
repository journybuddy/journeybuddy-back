package journeybuddy.spring.web.controller;

import io.swagger.annotations.ApiOperation;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.PostConverter;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.service.PostService.PostCommandService;
import journeybuddy.spring.web.dto.PostDTO.PostPagingDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostCommandService postCommandService;
    private final PostRepository postRepository;

    @ApiOperation(value = "글 전체 조회")
    @GetMapping("/api/v1")
    public ApiResponse<List<PostResponseDTO>> getAll(@PageableDefault(size = 20, sort ="userId",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        //stream을 이용해서 엔티티를 응답객체로 변경
        List<PostResponseDTO> postResponseDTOS = posts.stream()
                .map(PostConverter::toPostResponseDTO)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(postResponseDTOS);
    }

    @ApiOperation(value = "글 전체 조회", notes = "post 전체 조회(1. 20개 페이징, 2.최신순 정렬)")
    @GetMapping("/api/v2222/posts")
    public ApiResponse<PageImpl<PostResponseDTO>> getAll2(@PageableDefault(size = 20, sort ="title",
            direction = Sort.Direction.DESC) Pageable pageable) {

        Logger logger = LoggerFactory.getLogger(PostController.class);
        logger.info("Pageable: page = {}, size = {}, sort = {}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        List<PostResponseDTO> posts = postCommandService.getPosts(pageable);
        return ApiResponse.onSuccess(new PageImpl<>(posts));
    }

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

    ///////////////////////////////////////////
    @GetMapping("/checkAllPaging")
    @ApiOperation("모든게시글확인")
    public Page<PostResponseDTO> findAll(@RequestBody PostPagingDTO postPagingDto){
        return postCommandService.findAllPost(postPagingDto);
    }





}
