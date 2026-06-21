package ktb.soo.project.domain.post.entity;

import jakarta.persistence.*;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_drafts")
@Getter
@NoArgsConstructor
public class PostDraft extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PostDraft(User user, String title, String content){
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public void updateDraft(String title, String content){
        this.title = title;
        this.content = content;
    }

}
