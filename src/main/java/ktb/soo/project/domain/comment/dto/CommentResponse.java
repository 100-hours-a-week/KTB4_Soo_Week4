package ktb.soo.project.domain.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime updatedAt;
    private Long userId;
    private String nickname;
    private List<CommentResponse> children;

    public CommentResponse(Long id, String content, LocalDateTime updatedAt, Long userId, String nickname, List<CommentResponse> children) {
        this.id = id;
        this.content = content;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.nickname = nickname;
        this.children = children;
    }
}
