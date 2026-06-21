package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHistoryRepository extends JpaRepository<PostHistory, Long> {
    int countByPostId(Long postId);
}
