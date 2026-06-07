package ktb.soo.project.domain.post.dto;

import lombok.Getter;

@Getter
public class DraftCreateRequest {
    private String title;
    private String content;

    public DraftCreateRequest(String title, String content){
        this.title = title;
        this.content = content;
    }
}
