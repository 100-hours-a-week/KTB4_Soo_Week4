package ktb.soo.project.domain.post.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public Post(Long userId, String title, String content, String status) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 임시저장 덮어쓰기용
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updateAt = LocalDateTime.now();
    }

    // 임시저장 -> 게시글 발행
    public void publish(String title, String content) {
        this.title = title;
        this.content = content;
        this.status = "PUBLISHED";
        this.updateAt = LocalDateTime.now();
    }
}
