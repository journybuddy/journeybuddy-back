package journeybuddy.spring.service.plan.vote;

import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.vote.Vote;
import journeybuddy.spring.domain.vote.VoteOption;
import journeybuddy.spring.domain.vote.VoteRecord;
import journeybuddy.spring.repository.plan.PlanRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.repository.vote.VoteOptionRepository;
import journeybuddy.spring.repository.vote.VoteRecordRepository;
import journeybuddy.spring.repository.vote.VoteRepository;
import journeybuddy.spring.web.dto.plan.vote.VoteRequestDTO;
import journeybuddy.spring.web.dto.plan.vote.VoteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteCommandServiceImpl implements VoteCommandService {

    private static final Logger log = LoggerFactory.getLogger(VoteCommandServiceImpl.class);
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final PlanRepository planRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;



    public VoteResponseDTO.VoteMakeResponseDTO makeVote(VoteRequestDTO voteRequestDTO, String userEmail) {
        Long planId = voteRequestDTO.getPlanId();
        List<VoteRequestDTO.VoteOptionRequestDTO> options = voteRequestDTO.getOptions();


        Optional<Plan> existingPlan = planRepository.findById(planId);
        if (existingPlan.isPresent()) {
            Plan plan = existingPlan.get();


            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));

            if (plan.getUser() == null) {
                throw new RuntimeException("플랜게설자가 없습니다.");
            }

            if (!plan.getUser().equals(user)) {
                throw new RuntimeException("플랜 주인만 투표를 개설할 수 있습니다.");
            }


            Vote vote = Vote.builder()
                    .plan(plan)
                    .title(voteRequestDTO.getTitle())
                    .description(voteRequestDTO.getDescription())
                    .startDate(voteRequestDTO.getStartDate())
                    .endDate(voteRequestDTO.getEndDate())
                    .build();


            voteRepository.save(vote);


            List<VoteResponseDTO.VoteInfoDTO> savedOptions = new ArrayList<>();
            for (VoteRequestDTO.VoteOptionRequestDTO optionRequest : options) {

                VoteOption voteOption = VoteOption.builder()
                        .vote(vote)
                        .optionText(optionRequest.getOptionText())
                        .voteCount(0)
                        .build();
                voteOptionRepository.save(voteOption);

                VoteResponseDTO.VoteInfoDTO optionDTO = new VoteResponseDTO.VoteInfoDTO();
                optionDTO.setOptionId(voteOption.getId());
                optionDTO.setOptionText(voteOption.getOptionText());

                savedOptions.add(optionDTO);
            }


            VoteResponseDTO.VoteMakeResponseDTO responseDTO = new VoteResponseDTO.VoteMakeResponseDTO();
            responseDTO.setPlanId(planId);
            responseDTO.setVoteId(vote.getId());
            responseDTO.setTitle(vote.getTitle());
            responseDTO.setDescription(vote.getDescription());
            responseDTO.setStartDate(vote.getStartDate());
            responseDTO.setEndDate(vote.getEndDate());
            responseDTO.setOptions(savedOptions);



            return responseDTO;
        } else {
            throw new RuntimeException("Plan with ID " + planId + " does not exist.");
        }
    }


    public List<VoteResponseDTO.VoteOptionResponseDTO> joinVote(Long voteId, List<Long> optionIds, Long userId) {

        Vote existingVote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));



        LocalDateTime today = LocalDateTime.now();


        if (today.isBefore(existingVote.getStartDate()) || today.isAfter(existingVote.getEndDate())) {
            throw new RuntimeException("Voting period for Vote ID " + voteId + " is not valid.");
        }


        if (optionIds == null) {
            optionIds = Collections.emptyList();
        }


        List<VoteRecord> existingRecords = voteRecordRepository.findByUserIdAndVoteOption_VoteId(userId, voteId);
        Set<Long> existingOptionIds = existingRecords.stream()
                .map(record -> record.getVoteOption().getId())
                .collect(Collectors.toSet());



        List<Long> newOptionIds = optionIds.stream()
                .filter(optionId -> !existingOptionIds.contains(optionId))
                .collect(Collectors.toList());




        if (newOptionIds.isEmpty()) {
            throw new RuntimeException("한 항목에 한번만 투표가능합니다.");
        }


        List<VoteOption> voteOptions = voteOptionRepository.findByVoteIdAndIdIn(voteId, newOptionIds);
        if (voteOptions.size() != newOptionIds.size()) {
            throw new RuntimeException("없는 투표항목입니다." + voteId);
        }

        List<VoteResponseDTO.VoteOptionResponseDTO> joinedVoteOptions = new ArrayList<>();

        for (VoteOption voteOption : voteOptions) {
            VoteRecord record = VoteRecord.builder()
                    .user(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User with ID " + userId + " does not exist.")))
                    .voteOption(voteOption)
                    .build();
            voteRecordRepository.save(record);


            voteOption.setVoteCount(voteOption.getVoteCount() + 1);
            voteOptionRepository.save(voteOption);


            VoteResponseDTO.VoteOptionResponseDTO responseDTO = VoteResponseDTO.VoteOptionResponseDTO.builder()
                    .id(voteOption.getId())
                    .optionText(voteOption.getOptionText())
                    .voteCount(voteOption.getVoteCount())
                    .userIds(Collections.singletonList(userId))
                    .build();


            joinedVoteOptions.add(responseDTO);


        }

        return joinedVoteOptions;
    }



    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));

        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);

        List<VoteResponseDTO.VoteOptionResponseDTO> result = new ArrayList<>();
        for (VoteOption option : voteOptions) {

            List<VoteRecord> voteRecords = voteRecordRepository.findByVoteOptionId(option.getId());

            List<Long> userIds = voteRecords.stream()
                    .map(record -> record.getUser().getId())
                    .collect(Collectors.toList());


            VoteResponseDTO.VoteOptionResponseDTO dto = VoteResponseDTO.VoteOptionResponseDTO.builder()
                    .id(option.getId())
                    .optionText(option.getOptionText())
                    .userIds(userIds)
                    .voteCount(option.getVoteCount())
                    .build();
            result.add(dto);
        }
        return result;
    }



    public Vote deleteVote(Long voteId,String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));
        Plan plan = vote.getPlan();

        if (!plan.getUser().equals(user)) {
            throw new RuntimeException("You can only create votes for plans you have created.");
        }
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);
        for (VoteOption option : voteOptions) {

            voteRecordRepository.deleteByVoteOptionId(option.getId());
        }
        voteRepository.delete(vote);

        return vote;
    }



    public List<VoteResponseDTO.VoteOptionResponseDTO> rollBackVote(Long voteId,String userEmail) {


        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));
        Long userId = user.getId();

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));



        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);



        List<Long> optionIds = voteOptions.stream()
                .map(VoteOption::getId)
                .collect(Collectors.toList());

        List<VoteRecord> voteRecords = voteRecordRepository.findByUser_IdAndVoteOption_IdIn(userId, optionIds);

        List<Long> records = voteRecords.stream()
                .map(VoteRecord::getId)
                .collect(Collectors.toList());


        List<Long> voteOptionIds = voteRecords.stream()
                .map(voteRecord -> voteRecord.getVoteOption().getId())
                .distinct()
                .collect(Collectors.toList());




        if(voteRecords.isEmpty()) {
            throw new RuntimeException("투표한적없습니다");
        }

        voteRecordRepository.deleteAll(voteRecords);



        List<VoteResponseDTO.VoteOptionResponseDTO> result = new ArrayList<>();
        for (VoteOption option : voteOptions) {

            if(voteOptionIds.contains(option.getId())) {

                List<VoteRecord> optionVoteRecords = voteRecordRepository.findByVoteOptionId(option.getId());
                List<Long> userIds = optionVoteRecords.stream()
                        .map(record -> record.getUser().getId())
                        .collect(Collectors.toList());


                option.setVoteCount(option.getVoteCount() - 1);


                voteOptionRepository.save(option);



                VoteResponseDTO.VoteOptionResponseDTO responseDTO = VoteResponseDTO.VoteOptionResponseDTO.builder()
                        .id(option.getId())
                        .optionText(option.getOptionText())
                        .voteCount(option.getVoteCount())
                        .userIds(userIds)
                        .build();

                result.add(responseDTO);
            }
        }

        return result;

    }
}