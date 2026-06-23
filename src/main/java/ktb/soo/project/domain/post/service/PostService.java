package ktb.soo.project.domain.post.service;

import ktb.soo.project.domain.comment.dto.CommentResponse;
import ktb.soo.project.domain.comment.entity.Comment;
import ktb.soo.project.domain.comment.repository.CommentRepository;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.post.entity.PostDraft;
import ktb.soo.project.domain.post.entity.PostHistory;
import ktb.soo.project.domain.post.entity.PostLike;
import ktb.soo.project.domain.post.repository.PostDraftRepository;
import ktb.soo.project.domain.post.repository.PostHistoryRepository;
import ktb.soo.project.domain.post.repository.PostLikeRepository;
import ktb.soo.project.domain.post.repository.PostRepository;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostDraftRepository postDraftRepository;
    private final PostHistoryRepository postHistoryRepository;
    private final PostLikeRepository postLikeRepository;

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
    @Transactional
    public Long createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        // 임시저장된 글을 바탕으로 최종 등록을 할 경우
        if (request.getDraftId() != null) {
            PostDraft postDraft = postDraftRepository.findById(request.getDraftId())
                    .orElseThrow(() -> new BusinessException("DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND, "임시저장 글을 찾을 수 없습니다."));

            if (!postDraft.getUser().getId().equals(userId)) {
                throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "해당 글에 대한 권한이 없습니다.");
            }

            Post newPost = new Post(user, request.getTitle(), request.getContent(), request.getImage());
            Post savedPost = postRepository.save(newPost);

            postDraftRepository.delete(postDraft);

            return savedPost.getId();

        } else {
            // 임시저장 없이 바로 발행할 경우
            Post newPost = new Post(user, request.getTitle(), request.getContent(), request.getImage());
            Post savedPost = postRepository.save(newPost);
            return savedPost.getId();
        }
    }

//    public List<Post> getMyDrafts(Long userId) {
//        return postRepository.findDraftsByUserId(userId);
//    }

    @Transactional
    public Long updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "본인이 작성한 글만 삭제할 수 있습니다.");
        }

        // 현재까지 쌓이 이력 개수 + 1해서 버전을 계산
        int nextVersion = postHistoryRepository.countByPostId(postId) + 1;
        PostHistory postHistory = new PostHistory(post, nextVersion);
        postHistoryRepository.save(postHistory);

        post.updatePost(request.getTitle(), request.getContent(), request.getImage());

        return post.getId();
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_POST_ACCESS", HttpStatus.FORBIDDEN, "본인이 작성한 글만 수정할 수 있습니다.");
        }

        post.softDelete();
    }

    @Transactional
    public void togglePostLike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));


        // 유저가 이미 좋아요 누른 이력이 있는지 확인
        Optional<PostLike> alreadyLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        if (alreadyLike.isPresent()) {
            postLikeRepository.delete(alreadyLike.get());
        } else {
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);
        }
    }

    public List<PostSliceResponse> getAllPublishedPosts() {
        List<Post> posts = postRepository.findAllPublishedPostsWithUser();
        return posts.stream()
                .map(post -> {
                    Long userId = (post.getUser() != null) ? post.getUser().getId() : null;
                    String nickname = (post.getUser() != null) ? post.getUser().getNickname() : "알 수 없는 사용자";

                    int likeCount = postLikeRepository.countByPostId(post.getId());
                    int commentCount = commentRepository.findByPostId(post.getId()).size();

                    return new PostSliceResponse(post, likeCount, commentCount, userId, nickname);
                })
                .toList();
    }

    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));


        Long postWriterId = (post.getUser() != null) ? post.getUser().getId() : null;
        String postWriterNickname = (post.getUser() != null) ? post.getUser().getNickname() : "알 수 없는 사용자";

        // 원댓글 + 원댓글 작성자 페치 조인 조회
        List<Comment> rootComments = commentRepository.findRootCommentsWithUserByPostId(postId);

        List<CommentResponse> commentDtos = rootComments.stream()
                .map(comment -> {
                    List<CommentResponse> childrenDtos = comment.getChildren().stream()
                            .map(child ->{
                                Long childWriterId = (child.getUser() != null) ? child.getUser().getId() : null;
                                String childNickname = (child.getUser() != null) ? child.getUser().getNickname() : "알 수 없는 사용자";
                                Long childCommentId = (child.getDeletedAt() == null) ? child.getId() : null;
                                return new CommentResponse(childCommentId, child.getContent(), child.getUpdatedAt(), childWriterId, childNickname, null);
                            })
                            .collect(Collectors.toList());
                    Long rootWriterId = (comment.getUser() != null) ? comment.getUser().getId() : null;
                    String rootNickname = (comment.getUser() != null) ? comment.getUser().getNickname() : "알 수 없는 사용자";
                    Long rootCommentId = (comment.getDeletedAt() == null) ? comment.getId() : null;
                    return new CommentResponse(rootCommentId, comment.getContent(), comment.getUpdatedAt(), rootWriterId, rootNickname, childrenDtos);
                })
                .collect(Collectors.toList());

        return new PostDetailResponse(post.getId(), post.getTitle(), post.getContent(), post.getUpdatedAt(), postWriterId, postWriterNickname, post.getViewCount(), commentDtos);

    }
}
