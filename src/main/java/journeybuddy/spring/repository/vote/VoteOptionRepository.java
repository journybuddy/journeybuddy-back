package journeybuddy.spring.repository.vote;

import journeybuddy.spring.domain.vote.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

    List<VoteOption> findByVoteId(Long voteId);
    List<VoteOption> findByVoteIdAndIdIn(Long voteId, List<Long>newOptionIds);
    Optional<VoteOption> findById(Long id);
}