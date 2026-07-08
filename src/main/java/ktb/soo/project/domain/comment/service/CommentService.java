package ktb.soo.project.domain.comment.service;

import ktb.soo.project.domain.comment.dto.CommentCreateRequest;
import ktb.soo.project.domain.comment.dto.CommentUpdateRequest;
import ktb.soo.project.domain.comment.entity.Comment;
import ktb.soo.project.domain.comment.repository.CommentRepository;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.post.repository.PostRepository;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long userId, Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment parentComment = null;

        // 대댓글을 달았을 때
        if (request.getParentId() != null) {
            parentComment =  commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

            if(!parentComment.getPost().getId().equals(postId)) {
                throw new BusinessException(ErrorCode.INVALID_COMMENT_POST_MISMATCH);
            }
        }

        // 일반 댓글(parentComment = null 또는 대댓글 생성
        Comment comment = new Comment(post,user, parentComment, request.getContent());
        Comment savedComment = commentRepository.save(comment);

        return savedComment.getId();
    }

    @Transactional
    public Long updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        comment.updateContent(request.getContent());
        return comment.getId();
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if(comment.getDeletedAt() != null){
            throw new BusinessException(ErrorCode.ALREADY_DELETED_COMMENT);
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        comment.softDelete();
    }
}
