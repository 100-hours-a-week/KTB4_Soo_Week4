package ktb.soo.project.domain.post.dto;

import ktb.soo.project.domain.comment.dto.CommentResponse;
import ktb.soo.project.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
    private Long userId;
    private String nickname;
    private int viewCount;
    private final List<CommentResponse> comments;

    public PostDetailResponse(Long postId, String title, String content, LocalDateTime updatedAt, Long userId ,String nickname,
                                 int viewCount, List<CommentResponse> comments) {
        this.id = postId;
        this.title = title;
        this.content = content;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.nickname = nickname;
        this.viewCount = viewCount;
        this.comments = comments;
    }
}
