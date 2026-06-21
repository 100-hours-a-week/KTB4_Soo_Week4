package ktb.soo.project.domain.post.service;

import ktb.soo.project.domain.comment.dto.CommentResponse;
import ktb.soo.project.domain.comment.repository.CommentRepository;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.post.entity.PostDraft;
import ktb.soo.project.domain.post.repository.PostDraftRepository;
import ktb.soo.project.domain.post.repository.PostRepository;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostDraftRepository postDraftRepository;

    // 최초 임시저장
    @Transactional
    public Long createDraft(Long userId, DraftCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        PostDraft draftPost = new PostDraft(user, request.getTitle(), request.getContent());
        PostDraft savedPost = postDraftRepository.save(draftPost);

        return savedPost.getId();
    }

    // 임시저장 덮어쓰기
    @Transactional
    public Long updateDraft(Long userId, Long draftId, DraftUpdateRequest request) {
        PostDraft postDraft = postDraftRepository.findById(draftId)
                .orElseThrow(() -> new BusinessException("DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND, "임시저장 글을 찾을 수 없습니다."));

        // 임시저장한 본인이 맞는지 검증
        if (!postDraft.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "해당 글에 대한 권한이 없습니다.");
        }

        postDraft.updateDraft(request.getTitle(), request.getContent());

        return postDraft.getId();
    }

    // 최종 게시글 작성 및 발행 로직
    public Long createPost(Long userId, PostCreateRequest request) {
        if (request.getDraftId() != null) {
            Post draftPost = postRepository.findById(request.getDraftId())
                    .orElseThrow(() -> new BusinessException("DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND, "임시저장 글을 찾을 수 없습니다."));

            if (!draftPost.getUserId().equals(userId)) {
                throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "해당 글에 대한 권한이 없습니다.");
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
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "본인이 작성한 글만 삭제할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent());

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "본인이 작성한 글만 수정할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    public void togglePostLike(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

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

    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        // 조회수 1 증가
        post.incrementHits();
        postRepository.save(post);

        String postWriterNickname = userRepository.findById(post.getUserId())
                .map(user -> user.getNickname())
                .orElse("알 수 없는 사용자");

        List<CommentResponse> allComments = commentRepository.findByPostId(postId).stream()
                .map(comment -> {
                    String commentWriterNickname = userRepository.findById(comment.getUserId())
                            .map(user -> user.getNickname())
                            .orElse("알 수 없는 사용자");
                    return new CommentResponse(comment, commentWriterNickname);
                })
                .toList();

        List<CommentResponse> rootComments = new ArrayList<>();

        Map<Long, CommentResponse> commentMap = new HashMap<>();
        allComments.forEach(c -> commentMap.put(c.getId(), c));

        for (CommentResponse comment : allComments) {
            if (comment.getParentId() == null) {
                rootComments.add(comment);
            } else {
                CommentResponse parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    parentComment.getReplies().add(comment);
                }
            }
        }

        return new PostDetailResponse(post, postWriterNickname, rootComments, allComments.size());
    }
}
