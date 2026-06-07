package ktb.soo.project.domain.post.dto;

import ktb.soo.project.domain.comment.dto.CommentResponse;
import ktb.soo.project.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final int likeCount;
    private final int hits;
    private final int commentCount;
    private final LocalDateTime createdAt;
    private final String writerNickname;
    private final List<CommentResponse> comments;

    public PostDetailResponse(Post post, String writerNickname, List<CommentResponse> comments, int totalCommentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikedUserIds().size();
        this.hits = post.getHits();
        this.commentCount = totalCommentCount;
        this.createdAt = post.getCreatedAt();
        this.writerNickname = writerNickname;
        this.comments = comments;
    }
}
