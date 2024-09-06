package journeybuddy.spring.web.dto.plan.vote;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponseDTO {
    Long id;
    String title;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    Long planId;
    List<VoteOptionResponseDTO> options;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate updatedAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VoteMakeResponseDTO {
        Long planId;
        Long voteId;
        String title;
        String description;
        @JsonFormat(pattern = "yy-MM-dd HH:mm")
        LocalDateTime startDate;
        @JsonFormat(pattern = "yy-MM-dd HH:mm")
        LocalDateTime endDate;
        private List<VoteResponseDTO.VoteInfoDTO> options;
    }



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoteInfoDTO {
        private String optionText;
        private Long optionId;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VoteOptionResponseDTO {
        Long id;
        String optionText;
        Integer voteCount;
        private List<Long> userIds;
    }
}