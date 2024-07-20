package journeybuddy.spring.web.controller;


import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.UserLikeConverter;
import journeybuddy.spring.domain.UserLike;
import journeybuddy.spring.service.UserLikeService.UserLikeCommandService;
import journeybuddy.spring.service.UserLikeService.UserLikeCommandServiceImpl;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeRequestDTO;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeResponesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/userlikes")
public class UserLikeRestController {

    private final UserLikeCommandService userLikeCommandService;
    private final UserLikeCommandServiceImpl userLikeCommandServiceImpl;

    @PostMapping("/save")
    public ApiResponse<UserLike> save(@RequestBody UserLikeRequestDTO requestDTO,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received request: {}", requestDTO);
        UserLike savedUserlike = UserLikeConverter.toUserLike(requestDTO);
        String userEmail = userDetails.getUsername();
        Long postId = requestDTO.getPostId();
        UserLike savedLikes = userLikeCommandService.saveLikes(userEmail,postId,savedUserlike);
        return ApiResponse.onSuccess(savedLikes);
    }

    @GetMapping("/myLikes")
    public ApiResponse<Page<UserLikeResponesDTO>> findMyLikes(@AuthenticationPrincipal UserDetails userDetails,
                                                              Pageable pageable) {
        String userEmail = userDetails.getUsername();
        Page<UserLikeResponesDTO> likesPage = userLikeCommandServiceImpl.findMyLike(userEmail,pageable);
        return ApiResponse.onSuccess(likesPage);
    }
}
