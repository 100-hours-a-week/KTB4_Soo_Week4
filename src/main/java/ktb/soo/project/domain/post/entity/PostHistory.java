package ktb.soo.project.domain.post.entity;

import jakarta.persistence.*;
import ktb.soo.project.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_histories")
@Getter
@NoArgsConstructor
public class PostHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    @Column(nullable = false)
    private int version;

    public PostHistory(Post post, int nextVersion) {
        this.post = post;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.version = nextVersion;
    }
}
