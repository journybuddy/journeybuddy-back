package journeybuddy.spring.service.plan.RandomURL;

import journeybuddy.spring.config.RandomUtils;
import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.plan.Random;
import journeybuddy.spring.repository.plan.PlanRepository;
import journeybuddy.spring.repository.plan.RandomURLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RandomURLServiceImpl {
    //랜덤 URL을 사용하여 친구를 초대한다.

    private final RandomURLRepository randomURLRepository;
    private final PlanRepository planRepository;
    private final RandomUtils randomUtils;

    public String createRandomURL(Long planId,String email) {

        String RandomString = RandomUtils.generateRandomURL(10,false);
        String RandomURL =  "https://journeyBuddy.com/invite/" + RandomString;

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));


        Random random = Random.builder()
                .URL(RandomURL)
                .plan(plan)
                .build();

        randomURLRepository.save(random);

        return RandomURL;
    }






}