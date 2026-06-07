package ktb.soo.project.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateRequest {
    @NotBlank(message = "COMMENT_CONTENT_EMPTY")
    private String content;

    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
