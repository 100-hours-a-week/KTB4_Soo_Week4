package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Long id);
    List<Post> findAll();
    void delete(Post post);
    List<Post> findDraftsByUserId(Long userId);
}
