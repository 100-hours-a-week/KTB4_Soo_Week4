package ktb.soo.project.domain.post.repository;

import ktb.soo.project.domain.post.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
@Repository
public class MemoryPostRepository implements PostRepository{
    private static final Map<Long, Post> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(sequence.incrementAndGet());
        }
        store.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(Post post) {
        store.remove(post.getId());
    }
}
