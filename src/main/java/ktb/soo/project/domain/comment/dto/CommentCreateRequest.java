package ktb.soo.project.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private Long parentId;

    @NotBlank(message = "COMMENT_CONTENT_EMPTY")
    private String content;

    public CommentCreateRequest(Long parentId, String content) {
        this.parentId = parentId;
        this.content = content;
    }
}
