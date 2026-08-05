package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    @Modifying
    @Query(value = """
            update post_views
            set viewed_at = :viewedAt,
                updated_at = :viewedAt
            where user_id = :userId
              and post_id = :postId
              and viewed_at < :threshold
            """, nativeQuery = true)
    int updateMemberViewIfExpired(@Param("userId") Long userId,
                                  @Param("postId") Long postId,
                                  @Param("threshold") LocalDateTime threshold,
                                  @Param("viewedAt") LocalDateTime viewedAt);

    @Modifying
    @Query(value = """
            insert ignore into post_views
                (user_id, guest_id, post_id, viewed_at, created_at, updated_at)
            values
                (:userId, null, :postId, :viewedAt, :viewedAt, :viewedAt)
            """, nativeQuery = true)
    int insertMemberViewIfAbsent(@Param("userId") Long userId,
                                 @Param("postId") Long postId,
                                 @Param("viewedAt") LocalDateTime viewedAt);

    @Modifying
    @Query(value = """
            update post_views
            set viewed_at = :viewedAt,
                updated_at = :viewedAt
            where guest_id = :guestId
              and post_id = :postId
              and viewed_at < :threshold
            """, nativeQuery = true)
    int updateGuestViewIfExpired(@Param("guestId") String guestId,
                                 @Param("postId") Long postId,
                                 @Param("threshold") LocalDateTime threshold,
                                 @Param("viewedAt") LocalDateTime viewedAt);

    @Modifying
    @Query(value = """
            insert ignore into post_views
                (user_id, guest_id, post_id, viewed_at, created_at, updated_at)
            values
                (null, :guestId, :postId, :viewedAt, :viewedAt, :viewedAt)
            """, nativeQuery = true)
    int insertGuestViewIfAbsent(@Param("guestId") String guestId,
                                @Param("postId") Long postId,
                                @Param("viewedAt") LocalDateTime viewedAt);
}
