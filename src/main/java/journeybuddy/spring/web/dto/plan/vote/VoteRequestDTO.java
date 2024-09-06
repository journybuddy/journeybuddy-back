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
public class VoteRequestDTO {
    Long planId;
    String title;
    String description;
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    LocalDateTime startDate;
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    LocalDateTime endDate;
    private List<VoteOptionRequestDTO> options;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoteOptionRequestDTO {
        private String optionText;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JoinVoteRequestDTO {
        private Long voteId;
        private List<Long> optionIds;
    }
}