package journeybuddy.spring.domain;


import jakarta.persistence.*;
import journeybuddy.spring.domain.common.BaseEntity;
import journeybuddy.spring.domain.mapping.Scrap;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<UserLike> userLikeList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Scrap> scrapList = new ArrayList<>();



    @Builder
    public Post(Long id, String title, String body, User user, List<Comment> comments, List<UserLike> likes) {
        this.id = id;
        this.title = title;
        this.content = getContent();
        this.user = user;

    }

    public Post(Long id, String title, String content, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static Post of(String title, String content, User user) {
        Post entity = new Post();
        entity.setTitle(title);
        entity.setContent(content);
        entity.setUser(user);
        return entity;
    }


}
