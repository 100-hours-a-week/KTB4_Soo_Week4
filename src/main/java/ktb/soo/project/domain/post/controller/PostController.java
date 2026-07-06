package ktb.soo.project.domain.post.controller;

import jakarta.validation.Valid;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.service.PostService;
import ktb.soo.project.global.response.ApiResponse;
import ktb.soo.project.global.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public ResponseEntity<ApiResponse<Long>> createDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody DraftCreateRequest request) {

        Long userId = userDetails.getUser().getId();
        Long draftId = postService.createDraft(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("DRAFT_SAVE_SUCCESS", draftId));
    }

    @PutMapping("/draft/{draftId}")
    public ResponseEntity<ApiResponse<Long>> updateDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long draftId,
            @RequestBody DraftUpdateRequest request) {

        Long userId = userDetails.getUser().getId();
        Long updateDraftId =  postService.updateDraft(userId, draftId, request);
        return ResponseEntity.ok(ApiResponse.of("DRAFT_UPDATE_SUCCESS", updateDraftId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PostCreateRequest request) {

        Long userId = userDetails.getUser().getId();
        Long postId = postService.createPost(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATE_SUCCESS", postId));
    }

//    @GetMapping("/drafts")
//    public ResponseEntity<ApiResponse<List<Post>>> getMyDrafts(
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Long userId = userDetails.getUser().getId();
//        List<Post> drafts = postService.getMyDrafts(userId);
//        return ResponseEntity.ok(ApiResponse.of("DRAFT_FETCH_SUCCESS", drafts));
//    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request) {

        Long userId = userDetails.getUser().getId();
        Long updatedPostId = postService.updatePost(userId, postId, request);
        return ResponseEntity.ok(ApiResponse.of("POST_UPDATE_SUCCESS", updatedPostId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        Long userId = userDetails.getUser().getId();
        postService.deletePost(userId, postId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> togglePostLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        Long userId = userDetails.getUser().getId();
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
