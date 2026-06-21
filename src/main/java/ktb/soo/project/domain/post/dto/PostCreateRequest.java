package ktb.soo.project.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostCreateRequest {
    private Long draftId;

    @NotBlank(message = "TITLE_EMPTY")
    @Size(max = 26, message = "INVALID_TITLE_LENGTH")
    private String title;

    @NotBlank(message = "CONTENT_EMPTY")
    private String content;

    private String image;

    public PostCreateRequest(Long draftId, String title, String content, String image) {
        this.draftId = draftId;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
