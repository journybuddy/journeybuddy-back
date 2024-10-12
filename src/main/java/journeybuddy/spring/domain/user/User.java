package journeybuddy.spring.domain.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import journeybuddy.spring.domain.common.BaseEntity;
import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.community.Scrap;
import journeybuddy.spring.domain.community.UserLike;
import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.vote.Vote;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false,unique = true)
    private String email;

//    @Column(nullable = false)
    private String bio;


    private String password;


    @Column(name = "profile_image", nullable = true)
    private String profile_image;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLike> userLikeList = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vote> voteList = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Plan> plans = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scrapList = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
    )

    @JsonIgnore
    private List<Role> roles;
}

