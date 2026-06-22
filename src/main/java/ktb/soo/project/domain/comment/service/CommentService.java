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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        Comment parentComment = null;

        // 대댓글을 달았을 때
        if (request.getParentId() != null) {
            parentComment =  commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("PARENT_COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "답글을 달려는 원댓글이 존재하지 않습니다."));

            if(!parentComment.getPost().getId().equals(postId)) {
                throw new BusinessException("INVALID_COMMENT_POST_MISMATCH", HttpStatus.BAD_REQUEST, "타 게시글의 댓글에는 답글을 달 수 없습니다.");
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
                .orElseThrow(() -> new BusinessException("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "수정하려는 댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_COMMENT_ACCESS", HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.updateContent(request.getContent());
        return comment.getId();
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "삭제하려는 댓글이 존재하지 않습니다."));

        if(comment.getDeletedAt() != null){
            throw new BusinessException("ALREADY_DELETED_COMMENT", HttpStatus.BAD_REQUEST, "이미 삭제된 댓글입니다.");
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED_COMMENT_ACCESS", HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        comment.softDelete();
    }
}
