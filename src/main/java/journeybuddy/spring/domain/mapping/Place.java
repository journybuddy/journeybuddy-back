package journeybuddy.spring.domain.mapping;


import jakarta.persistence.*;
import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.domain.Schedule;
import journeybuddy.spring.domain.Vote;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Place { //원하는 장소 직접입력


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    private Double latitude;
    private Double longtitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}
