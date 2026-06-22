package ktb.soo.project.domain.post.repository;


import ktb.soo.project.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.user u " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.isBlinded = false " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllPublishedPostsWithUser();
}
