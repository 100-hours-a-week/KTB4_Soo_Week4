package ktb.soo.project.domain.comment.repository;

import ktb.soo.project.domain.comment.entity.Comment;
import ktb.soo.project.domain.post.repository.PostCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    int countByPostIdAndDeletedAtIsNull(Long postId);

    @Query("select c from Comment c " +
            "left join fetch c.user " +
            "where c.post.id = :postId and c.parent is null")
    List<Comment> findRootCommentsWithUserByPostId(@Param("postId") Long postId);

    @Query("select c.post.id as postId, count(c) as count from Comment c " +
            "where c.post.id in :postIds and c.deletedAt is null " +
            "group by c.post.id")
    List<PostCountProjection> countGroupByPostIds(@Param("postIds") List<Long> postIds);
}
