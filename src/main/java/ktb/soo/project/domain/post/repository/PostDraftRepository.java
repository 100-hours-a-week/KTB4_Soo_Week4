package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.PostDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostDraftRepository extends JpaRepository<PostDraft, Long> {
}
