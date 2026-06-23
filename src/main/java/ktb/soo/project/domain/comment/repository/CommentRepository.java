package ktb.soo.project.domain.comment.repository;

import ktb.soo.project.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    @Query("select c from Comment c " +
            "join fetch c.user " +
            "where c.post.id = :postId and c.parent is null")
    List<Comment> findRootCommentsWithUserByPostId(@Param("postId") Long postId);
}
