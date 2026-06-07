package ktb.soo.project.domain.comment.dto;

import ktb.soo.project.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponse {
    private final Long id;
    private final Long parentId; // null이면 일반 댓글, 값이 있으면 대댓글
    private final String content;
    private final LocalDateTime createdAt;
    private final String writerNickname;
    private final List<CommentResponse> replies = new ArrayList<>();

    public CommentResponse(Comment comment, String writerNickname) {
        this.id = comment.getId();
        this.parentId = comment.getParentId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.writerNickname = writerNickname;
    }
}
