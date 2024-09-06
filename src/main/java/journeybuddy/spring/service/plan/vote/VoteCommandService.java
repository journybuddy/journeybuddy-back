package journeybuddy.spring.service.plan.vote;

import journeybuddy.spring.domain.vote.Vote;
import journeybuddy.spring.domain.vote.VoteOption;
import journeybuddy.spring.web.dto.plan.vote.VoteRequestDTO;
import journeybuddy.spring.web.dto.plan.vote.VoteResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteCommandService {

    public VoteResponseDTO.VoteMakeResponseDTO makeVote(VoteRequestDTO voteRequestDTO, String userEmail);
    public List<VoteResponseDTO.VoteOptionResponseDTO> joinVote(Long voteId, List<Long> optionIds, Long userId);
    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId);
    public Vote deleteVote(Long voteId,String userEmail);




}
