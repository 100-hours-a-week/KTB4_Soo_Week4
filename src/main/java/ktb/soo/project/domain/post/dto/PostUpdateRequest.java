package ktb.soo.project.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostUpdateRequest {
    @NotBlank(message = "TITLE_EMPTY")
    @Size(max = 26, message = "INVALID_TITLE_LENGTH")
    private String title;

    @NotBlank(message = "CONTENT_EMPTY")
    private String content;

    private String image;

    public PostUpdateRequest(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
