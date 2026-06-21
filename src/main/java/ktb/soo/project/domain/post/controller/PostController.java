package ktb.soo.project.domain.post.controller;

import jakarta.validation.Valid;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.post.service.PostService;
import ktb.soo.project.global.annotation.LoginUser;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public ResponseEntity<ApiResponse<Long>> createDraft(
            @LoginUser Long userId,
            @RequestBody DraftCreateRequest request) {

        Long draftId = postService.createDraft(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("DRAFT_SAVE_SUCCESS", draftId));
    }

    @PutMapping("/draft/{draftId}")
    public ResponseEntity<ApiResponse<Long>> updateDraft(
            @LoginUser Long userId,
            @PathVariable Long draftId,
            @RequestBody DraftUpdateRequest request) {

        Long updateDraftId =  postService.updateDraft(userId, draftId, request);
        return ResponseEntity.ok(ApiResponse.of("DRAFT_UPDATE_SUCCESS", updateDraftId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @LoginUser Long userId,
            @RequestBody @Valid PostCreateRequest request) {

        Long postId = postService.createPost(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATE_SUCCESS", postId));
    }

    @GetMapping("/drafts")
    public ResponseEntity<ApiResponse<List<Post>>> getMyDrafts(@LoginUser Long userId) {
        List<Post> drafts = postService.getMyDrafts(userId);
        return ResponseEntity.ok(ApiResponse.of("DRAFT_FETCH_SUCCESS", drafts));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @LoginUser Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request) {

        Long updatedPostId = postService.updatePost(userId, postId, request);
        return ResponseEntity.ok(ApiResponse.of("POST_UPDATE_SUCCESS", updatedPostId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @LoginUser Long userId,
            @PathVariable Long postId) {

        postService.deletePost(userId, postId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> togglePostLike(
            @LoginUser Long userId,
            @PathVariable Long postId) {

        postService.togglePostLike(userId, postId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostSliceResponse>>> getAllPosts() {
        List<PostSliceResponse> responses = postService.getAllPublishedPosts();
        return ResponseEntity.ok(ApiResponse.of("POST_FETCH_SUCCESS", responses));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(@PathVariable Long postId) {
        PostDetailResponse response = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.of("POST_DETAIL_FETCH_SUCCESS", response));

    }
}
