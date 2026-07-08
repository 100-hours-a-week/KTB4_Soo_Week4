package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    int countByPostId(Long postId);

    @Query("select pl.post.id, count(pl) from PostLike pl " +
            "where pl.post.id in :postIds " +
            "group by pl.post.id")
    Map<Long, Long> countGroupByUserPostIds(@Param("postIds") List<Long> postIds);
}
