package ktb.soo.project.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ktb.soo.project.domain.post.dto.*;
import ktb.soo.project.domain.post.service.PostService;
import ktb.soo.project.domain.post.support.GuestCookieManager;
import ktb.soo.project.domain.post.support.GuestCookieManager.GuestCookieResolution;
import ktb.soo.project.global.response.ApiResponse;
import ktb.soo.project.global.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static ktb.soo.project.domain.post.support.GuestCookieManager.COOKIE_NAME;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final GuestCookieManager guestCookieManager;

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
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @CookieValue(name = COOKIE_NAME, required = false) String guestId,
                                                                         HttpServletRequest request,
                                                                         @PathVariable Long postId) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        Optional<ResponseCookie> responseCookie = Optional.empty();

        // 비회원
        if (userId == null) {
            GuestCookieResolution resolution = guestCookieManager.resolve(guestId, request.isSecure());
            guestId = resolution.guestId();
            responseCookie = resolution.responseCookie();
        }

        PostDetailResponse response = postService.getPostDetail(userId, guestId, postId);
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();

        responseCookie.ifPresent(cookie ->
                responseBuilder.header(HttpHeaders.SET_COOKIE, cookie.toString())
        );

        return responseBuilder.body(ApiResponse.of("POST_DETAIL_FETCH_SUCCESS", response));
    }
}
