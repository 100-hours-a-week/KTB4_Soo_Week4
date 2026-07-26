package ktb.soo.project.domain.post.service;

import ktb.soo.project.domain.comment.dto.CommentResponse;
import ktb.soo.project.domain.comment.entity.Comment;
import ktb.soo.project.domain.comment.repository.CommentRepository;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.entity.*;
import ktb.soo.project.domain.post.repository.*;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final PostViewRepository postViewRepository;

    // 최초 임시저장
    @Transactional
    public Long createDraft(Long userId, DraftCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        PostDraft draftPost = new PostDraft(user, request.getTitle(), request.getContent());
        PostDraft savedPost = postDraftRepository.save(draftPost);

        return savedPost.getId();
    }

    // 임시저장 덮어쓰기
    @Transactional
    public Long updateDraft(Long userId, Long draftId, DraftUpdateRequest request) {
        PostDraft postDraft = postDraftRepository.findById(draftId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        // 임시저장한 본인이 맞는지 검증
        if (!postDraft.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        postDraft.updateDraft(request.getTitle(), request.getContent());

        return postDraft.getId();
    }

    // 최종 게시글 작성 및 발행 로직
    @Transactional
    public Long createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 임시저장된 글을 바탕으로 최종 등록을 할 경우
        if (request.getDraftId() != null) {
            PostDraft postDraft = postDraftRepository.findById(request.getDraftId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

            if (!postDraft.getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
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


    @Transactional
    public Long updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        post.softDelete();
    }

    @Transactional
    public void togglePostLike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));


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

        if(posts.isEmpty()){
            return Collections.emptyList();
        }

        List<Long> postIds = new ArrayList<>();
        for (Post post : posts) {
            postIds.add(post.getId());
        }

        Map<Long, Long> likeCountMap = postLikeRepository.countGroupByUserPostIds(postIds).stream()
                .collect(Collectors.toMap(PostCountProjection::getPostId, PostCountProjection::getCount));
        Map<Long, Long> commentCountMap = commentRepository.countGroupByPostIds(postIds).stream()
                .collect(Collectors.toMap(PostCountProjection::getPostId, PostCountProjection::getCount));

        List<PostSliceResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            Long userId = (post.getUser() != null) ? post.getUser().getId() : null;
            String nickname = (post.getUser() != null) ? post.getUser().getNickname() : "알 수 없는 사용자";

            int likeCount = likeCountMap.getOrDefault(post.getId(), 0L).intValue();
            int commentCount = commentCountMap.getOrDefault(post.getId(), 0L).intValue();

            PostSliceResponse responseDto = new PostSliceResponse(post, likeCount, commentCount, userId, nickname);

            responses.add(responseDto);
        }

        return responses;
    }

    @Transactional
    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        handleViewCount(userId, post);

        return convertToDetailResponse(userId, post);
    }

    private void handleViewCount(Long userId, Post post) {
        // 비회원
        if (userId == null) {
            post.increaseViewCount();
            return;
        }

        // 회원인 경우
        User userProxy = userRepository.getReferenceById(userId);
        LocalDateTime now = LocalDateTime.now();
        Optional<PostView> postViewOpt = postViewRepository.findByUserIdAndPostId(userId, post.getId());

        if (postViewOpt.isEmpty()) {
            // 최초 조회
            PostView newView = new PostView(userProxy, post);
            postViewRepository.save(newView);
            post.increaseViewCount();
        } else {
            // 다시 조회
            PostView existView = postViewOpt.get();
            if (existView.getViewedAt().isBefore(now.minusHours(24))) {
                existView.updateViewedAt();
                post.increaseViewCount();
            }
        }
    }

    private PostDetailResponse convertToDetailResponse(Long currentUserId, Post post) {
        // 게시글 작성자 검증
        Long postWriterId = (post.getUser() != null) ? post.getUser().getId() : null;
        String postWriterNickname = (post.getUser() != null) ? post.getUser().getNickname() : "알 수 없는 사용자";
        boolean isPostAuthor = Objects.equals(currentUserId, postWriterId) && currentUserId != null;
        boolean isLiked = currentUserId != null
                && postLikeRepository.findByUserIdAndPostId(currentUserId, post.getId()).isPresent();
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostIdAndDeletedAtIsNull(post.getId());

        // 원댓글 + 원댓글 작성자 페치 조인 조회
        List<Comment> rootComments = commentRepository.findRootCommentsWithUserByPostId(post.getId());
        List<CommentResponse> commentDtos = new ArrayList<>();

        // 원댓글 리스트 루프
        for (Comment comment : rootComments) {
            List<CommentResponse> childrenDtos = new ArrayList<>();

            // 대댓글 루프
            for (Comment child : comment.getChildren()) {
                Long childWriterId = (child.getUser() != null) ? child.getUser().getId() : null;
                String childNickname = (child.getUser() != null) ? child.getUser().getNickname() : "알 수 없는 사용자";
                Long childCommentId = (child.getDeletedAt() == null) ? child.getId() : null;

                CommentResponse childDto = new CommentResponse(
                        childCommentId,
                        child.getContent(),
                        child.getUpdatedAt(),
                        childWriterId,
                        childNickname,
                        Objects.equals(currentUserId, childWriterId) && currentUserId != null,
                        null
                );
                childrenDtos.add(childDto);
            }

            // 원댓글 작성자 검증
            Long rootWriterId = (comment.getUser() != null) ? comment.getUser().getId() : null;
            String rootNickname = (comment.getUser() != null) ? comment.getUser().getNickname() : "알 수 없는 사용자";
            Long rootCommentId = (comment.getDeletedAt() == null) ? comment.getId() : null;

            CommentResponse rootDto = new CommentResponse(
                    rootCommentId,
                    comment.getContent(),
                    comment.getUpdatedAt(),
                    rootWriterId,
                    rootNickname,
                    Objects.equals(currentUserId, rootWriterId) && currentUserId != null,
                    childrenDtos
            );
            commentDtos.add(rootDto);
        }

        // 최종 DTO 반환
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUpdatedAt(),
                postWriterId,
                postWriterNickname,
                post.getViewCount(),
                likeCount,
                commentCount,
                isPostAuthor,
                isLiked,
                commentDtos
        );
    }

}
