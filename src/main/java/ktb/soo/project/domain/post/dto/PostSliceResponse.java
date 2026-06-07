package ktb.soo.project.domain.post.dto;

import ktb.soo.project.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSliceResponse {
    private final Long id;
    private final String title;
    private final int likeCount;
    private final int commentCount;
    private final int hits;
    private final LocalDateTime createdAt;
    private final String writerNickname;

    public PostSliceResponse(Post post, String writerNickname, int commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.likeCount = post.getLikedUserIds().size();
        this.commentCount = commentCount;
        this.hits = post.getHits();
        this.createdAt = post.getCreatedAt();
        this.writerNickname = writerNickname;
    }
}
