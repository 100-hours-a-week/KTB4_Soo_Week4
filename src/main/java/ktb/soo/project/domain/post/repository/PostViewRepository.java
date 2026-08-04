package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    // 유저가 특정 게시글을 본 이력이 있는지 조회
    Optional<PostView> findByUserIdAndPostId(Long userId, Long postId);

    // 비회원이 특정 게시글을 본 이력이 있는지 조회
    Optional<PostView> findByGuestIdAndPostId(String guestId, Long postId);
}
