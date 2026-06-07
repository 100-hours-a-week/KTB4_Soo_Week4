package ktb.soo.project.domain.post.entity;

import lombok.Getter;

@Getter
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String status;

    public Post(Long userId, String title, String content, String status) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 임시저장 덮어쓰기용
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 임시저장 -> 게시글 발행
    public void publish(String title, String content) {
        this.title = title;
        this.content = content;
        this.status = "PUBLISHED";
    }
}
