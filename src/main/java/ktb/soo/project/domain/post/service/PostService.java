package ktb.soo.project.domain.post.service;

import ktb.soo.project.domain.comment.repository.CommentRepository;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.post.repository.PostRepository;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 최초 임시저장
    public Long createDraft(Long userId, DraftCreateRequest request) {
        Post draftPost = new Post(userId, request.getTitle(), request.getContent(), "DRAFT");
        Post savedPost = postRepository.save(draftPost);
        return savedPost.getId();
    }

    // 임시저장 덮어쓰기
    public Long updateDraft(Long userId, Long draftId, DraftUpdateRequest request) {
        Post post = postRepository.findById(draftId)
                .orElseThrow(() -> new BusinessException("DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND));

        // 임시저장한 본인이 맞는지 검증
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN);
        }

        post.update(request.getTitle(), request.getContent());
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    // 최종 게시글 작성 및 발행 로직
    public Long createPost(Long userId, PostCreateRequest request) {
        if (request.getDraftId() != null) {
            Post draftPost = postRepository.findById(request.getDraftId())
                    .orElseThrow(() -> new BusinessException("DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND));

            if (!draftPost.getUserId().equals(userId)) {
                throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN);
            }

            draftPost.publish(request.getTitle(), request.getContent());
            Post savedPost = postRepository.save(draftPost);
            return savedPost.getId();

        } else {
            // 처음부터 바로 발행하는 경우
            Post newPost = new Post(userId, request.getTitle(), request.getContent(), "PUBLISHED");
            Post savedPost = postRepository.save(newPost);
            return savedPost.getId();
        }
    }

    public List<Post> getMyDrafts(Long userId) {
        return postRepository.findDraftsByUserId(userId);
    }

    public Long updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN);
        }

        post.update(request.getTitle(), request.getContent());

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN);
        }

        postRepository.delete(post);
    }

    public void togglePostLike(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND));

        post.toggleLike(userId);

        postRepository.save(post);
    }

    public List<PostSliceResponse> getAllPublishedPosts() {
        return postRepository.findAll().stream()
                .filter(post -> "PUBLISHED".equals(post.getStatus()))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(post -> {
                    String nickname = userRepository.findById(post.getUserId())
                            .map(user -> user.getNickname())
                            .orElse("알 수 없는 사용자");

                    int commentCount = commentRepository.findByPostId(post.getId()).size();

                    return new PostSliceResponse(post, nickname, commentCount);
                })
                .toList();
    }
}
