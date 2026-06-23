package ktb.soo.project.domain.comment.entity;

import jakarta.persistence.*;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@BatchSize(size = 100)
@Table(name = "comments")
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    public Comment(Post post, User user, Comment parent, String content) {
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.content = content;
    }


    public void updateContent(String content){
        this.content = content;
    }

    public void softDelete(){
        this.deletedAt = LocalDateTime.now();
    }
}
