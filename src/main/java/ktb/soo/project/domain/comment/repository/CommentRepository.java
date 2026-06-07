package ktb.soo.project.domain.comment.repository;

import ktb.soo.project.domain.comment.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    List<Comment> findByPostId(Long postId);
}
