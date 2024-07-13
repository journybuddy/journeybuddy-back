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
    private String address;  // 주소가 기본 키로 사용됨
    private String name;
    private double latitude;
    private double longitude;
}
