package ktb.soo.project.domain.post.dto;

import lombok.Getter;

@Getter
public class DraftUpdateRequest {
    private String title;
    private String content;

    public DraftUpdateRequest(String title, String content){
        this.title = title;
        this.content = content;
    }
}
