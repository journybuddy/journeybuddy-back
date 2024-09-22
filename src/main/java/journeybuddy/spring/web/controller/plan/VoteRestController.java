package journeybuddy.spring.web.controller.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.vote.VoteOption;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.repository.vote.VoteRepository;
import journeybuddy.spring.service.plan.vote.VoteCommandService;
import journeybuddy.spring.service.plan.vote.VoteCommandServiceImpl;
import journeybuddy.spring.web.dto.plan.vote.VoteRequestDTO;
import journeybuddy.spring.web.dto.plan.vote.VoteResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ControllerAdvice
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteRestController {
    private final VoteCommandService voteCommandService;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final VoteCommandServiceImpl voteCommandServiceImpl;

    @PostMapping("/make_vote")
    @Operation(summary = "투표만들기", description =  "yy-MM-dd HH:mm 형식으로 입력해주세요")
    public ApiResponse<VoteResponseDTO.VoteMakeResponseDTO> makeVote(
            @RequestBody VoteRequestDTO voteRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {


        String userEmail = userDetails.getUsername();
        log.info("Received VoteRequestDTO: " + userEmail);
        VoteResponseDTO.VoteMakeResponseDTO options = voteCommandService.makeVote(voteRequestDTO, userEmail);
        return ApiResponse.onSuccess(options);
    }

    @PostMapping("/join_vote")
    @Operation(summary = "투표 참여하기", description = "투표 참여하기")
    public ApiResponse<List<VoteResponseDTO.VoteOptionResponseDTO>>joinVote(
            @RequestBody VoteRequestDTO.JoinVoteRequestDTO joinVoteRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {


        String userEmail = userDetails.getUsername();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        log.info("투표한 사용자: " + userEmail);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            long userId = user.getId();

            Long voteId = joinVoteRequestDTO.getVoteId();
            List<Long> optionIds = joinVoteRequestDTO.getOptionIds();

            if (optionIds == null) {
                optionIds = Collections.emptyList();
            }
            List<VoteResponseDTO.VoteOptionResponseDTO> options = voteCommandService.joinVote(voteId, optionIds, userId);
            return ApiResponse.onSuccess(options);
        } else {
            return ApiResponse.onFailure("err","err",null);
        }
    }

    @GetMapping("/{voteId}/vote_result")
    @Operation(summary = "투표 결과 확인하기", description = "투표 결과 확인하기")
    public ApiResponse<List<VoteResponseDTO.VoteOptionResponseDTO>> checkResult(@PathVariable Long voteId){
        List<VoteResponseDTO.VoteOptionResponseDTO> check = voteCommandService.checkVoteResult(voteId);
        return ApiResponse.onSuccess(check);
    }

    @DeleteMapping("/{voteId}/delete")
    @Operation(summary = "투표 삭제하기", description = "투표 삭제하기")
    public ApiResponse<?> deleteVote(@PathVariable Long voteId,
                                     @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        voteCommandService.deleteVote(voteId,userEmail);
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping("/{voteId}/retry")
    @Operation(summary = "투표 취소하기", description = "투표 취소하기")
    public ApiResponse<List<VoteResponseDTO.VoteOptionResponseDTO>> retryVote(
            @PathVariable Long voteId,@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();

        List<VoteResponseDTO.VoteOptionResponseDTO> check = voteCommandServiceImpl.rollBackVote(voteId,userEmail);
        return ApiResponse.onSuccess(check);
    }
}