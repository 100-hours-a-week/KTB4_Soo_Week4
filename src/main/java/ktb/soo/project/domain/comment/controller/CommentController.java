package ktb.soo.project.domain.comment.controller;

import jakarta.validation.Valid;
import ktb.soo.project.domain.comment.dto.CommentCreateRequest;
import ktb.soo.project.domain.comment.dto.CommentUpdateRequest;
import ktb.soo.project.domain.comment.service.CommentService;
import ktb.soo.project.global.annotation.LoginUser;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createComment(
            @LoginUser Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequest request) {

        Long commentId = commentService.createComment(userId, postId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("COMMENT_CREATE_SUCCESS", commentId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Long>> updateComment(
            @LoginUser Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request) {

        Long updatedCommentId = commentService.updateComment(userId, commentId, request);
        return ResponseEntity.ok(ApiResponse.of("COMMENT_UPDATE_SUCCESS", updatedCommentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @LoginUser Long userId,
            @PathVariable Long commentId) {

        commentService.deleteComment(userId, commentId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
