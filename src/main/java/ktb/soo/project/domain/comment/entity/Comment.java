package ktb.soo.project.domain.comment.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId; // 부모 댓글 ID(일반 댓글은 null)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public Comment(Long postId, Long userId, Long parentId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void updateContent(String content){
        this.content = content;
        this.updateAt = LocalDateTime.now();
    }
}
