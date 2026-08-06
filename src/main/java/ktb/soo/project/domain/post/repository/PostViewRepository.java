package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    Optional<PostView> findByUserIdAndPostId(Long userId, Long postId);

    Optional<PostView> findByGuestIdAndPostId(String guestId, Long postId);
}
