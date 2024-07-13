package journeybuddy.spring.domain;


import jakarta.persistence.*;
import journeybuddy.spring.domain.mapping.Place;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "address", referencedColumnName = "address", nullable = false)
    private Place place;

}
