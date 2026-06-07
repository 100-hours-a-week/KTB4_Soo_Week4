package ktb.soo.project.domain.comment.repository;

import ktb.soo.project.domain.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryCommentRepository implements CommentRepository{
    private static final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(sequence.incrementAndGet());
        }
        store.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return store.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
    }
}
