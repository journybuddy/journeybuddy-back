package journeybuddy.spring.web.controller;


import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.ScrapConverter;
import journeybuddy.spring.domain.mapping.Scrap;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.ScrapRepository;
import journeybuddy.spring.service.ScrapService.ScrapCommandService;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapRequestDTO;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/scraps")
public class ScrapRestController {
    private final ScrapRepository scrapRepository;
    private final ScrapCommandService scrapCommandService;
    private final PostRepository postRepository;

    @PostMapping("/save")
    public ApiResponse<Scrap> save(@RequestBody ScrapRequestDTO scrapRequestDTO,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Scrap savedScrap = ScrapConverter.toScrap(scrapRequestDTO);
        String userEmail = userDetails.getUsername();
        Long postId = scrapRequestDTO.getPostId();
        savedScrap = scrapCommandService.saveScrap(userEmail,postId,savedScrap);
        return ApiResponse.onSuccess(savedScrap);
    }

    @GetMapping("/myScrap")
    public ApiResponse<Page<ScrapResponseDTO>> getMyScrap(@AuthenticationPrincipal UserDetails userDetails,
                                               @PageableDefault(size = 20, sort ="registeredAt",
                                                       direction = Sort.Direction.DESC) Pageable pageable) {
        String userEmail = userDetails.getUsername();
        Page<ScrapResponseDTO> scrapPage = scrapCommandService.findAll(userEmail,pageable);
        return ApiResponse.onSuccess(scrapPage);
    }
}
