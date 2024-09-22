package journeybuddy.spring.repository.vote;

import journeybuddy.spring.domain.vote.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    List<VoteRecord>  findByUser_IdAndVoteOption_IdIn(Long userId, List<Long> optionIds);
    List<VoteRecord> findByUserIdAndVoteOption_VoteId(Long userId,Long voteId);
    List<VoteRecord> findByVoteOptionId(Long voteOptionId);
    List<VoteRecord> deleteByVoteOptionId(Long id);

}
