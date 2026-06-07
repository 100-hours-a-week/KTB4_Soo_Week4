package ktb.soo.project.domain.comment.service;

import ktb.soo.project.domain.comment.dto.CommentCreateRequest;
import ktb.soo.project.domain.comment.dto.CommentUpdateRequest;
import ktb.soo.project.domain.comment.entity.Comment;
import ktb.soo.project.domain.comment.repository.CommentRepository;
import ktb.soo.project.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Long createComment(Long userId, Long postId, CommentCreateRequest request) {
        if (request.getParentId() != null) {
            commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("PARENT_COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        }

        Comment comment = new Comment(postId, userId, request.getParentId(), request.getContent());
        Comment savedComment = commentRepository.save(comment);

        return savedComment.getId();
    }

    public Long updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_COMMENT_ACCESS", HttpStatus.FORBIDDEN);
        }

        comment.updateContent(request.getContent());
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_COMMENT_ACCESS", HttpStatus.FORBIDDEN);
        }

        comment.updateContent("삭제된 댓글입니다.");
        commentRepository.save(comment);
    }
}
