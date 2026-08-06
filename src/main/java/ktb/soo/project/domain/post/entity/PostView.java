package ktb.soo.project.domain.post.entity;

import jakarta.persistence.*;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_views",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_view_user_post",
                        columnNames = {"user_id", "post_id"}
                ),
                @UniqueConstraint(
                        name = "uk_post_view_guest_post",
                        columnNames = {"guest_id", "post_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class PostView extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "guest_id", length = 36)
    private String guestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public PostView(User user, Post post) {
        this.user = user;
        this.post = post;
        this.viewedAt = LocalDateTime.now();
    }

    public PostView(String guestId, Post post) {
        this.guestId = guestId;
        this.post = post;
        this.viewedAt = LocalDateTime.now();
    }

    public void updateViewedAt() {
        this.viewedAt = LocalDateTime.now();
    }

}
