package journeybuddy.spring.domain;


import jakarta.persistence.*;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /*
    //dto - > entity로 바꾸는 메서드
    public static Comment of(User user, Post post, String comment) {
        Comment entity = Comment.builder()
                .user(user)
                .post(post)
                .comment(comment)
                .build();
        return entity;
    }
*/
}
