package ktb.soo.project.domain.post.entity;

import jakarta.persistence.*;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited = false;

    @Column(name = "report_count", nullable = false)
    private int reportCount = 0;

    @Column(name = "is_blinded", nullable = false)
    private boolean isBlinded = false;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Post(User user, String title, String content, String image) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public void updatePost(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.isEdited = true;
    }

    public void softDelete(){
        this.deletedAt = LocalDateTime.now();
    }


}
