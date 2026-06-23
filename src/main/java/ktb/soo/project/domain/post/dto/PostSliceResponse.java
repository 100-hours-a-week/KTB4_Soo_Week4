package ktb.soo.project.domain.post.dto;

import ktb.soo.project.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSliceResponse {
    private Long id;
    private String title;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private LocalDateTime updatedAt;
    private Long userId;
    private String nickname;

    public PostSliceResponse(Post post, int likeCount, int commentCount, Long userId, String nickname) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = post.getViewCount();
        this.updatedAt = post.getUpdatedAt();
        this.userId = userId;
        this.nickname = nickname;
    }
}
