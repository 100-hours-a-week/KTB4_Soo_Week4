package ktb.soo.project.domain.post.controller;

import jakarta.validation.Valid;
import ktb.soo.project.domain.post.dto.DraftCreateRequest;
import ktb.soo.project.domain.post.dto.DraftUpdateRequest;
import ktb.soo.project.domain.post.dto.PostCreateRequest;
import ktb.soo.project.domain.post.entity.Post;
import ktb.soo.project.domain.post.service.PostService;
import ktb.soo.project.global.annotation.LoginUser;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public ApiResponse<Long> createDraft(
            @LoginUser Long userId,
            @RequestBody DraftCreateRequest request) {

        Long draftId = postService.createDraft(userId, request);
        return ApiResponse.of("DRAFT_SAVE_SUCCESS", draftId);
    }

    @PutMapping("/draft/{draftId}")
    public ApiResponse<Long> updateDraft(
            @LoginUser Long userId,
            @PathVariable Long draftId,
            @RequestBody DraftUpdateRequest request) {

        Long updateDraftId =  postService.updateDraft(userId, draftId, request);
        return ApiResponse.of("DRAFT_UPDATE_SUCCESS", updateDraftId);
    }

    @PostMapping
    public ApiResponse<Long> createPost(
            @LoginUser Long userId,
            @RequestBody @Valid PostCreateRequest request) {

        Long postId = postService.createPost(userId, request);
        return ApiResponse.of("POST_CREATE_SUCCESS", postId);
    }

    @GetMapping("/drafts")
    public ApiResponse<List<Post>> getMyDrafts(@LoginUser Long userId) {
        List<Post> drafts = postService.getMyDrafts(userId);
        return ApiResponse.of("DRAFT_FETCH_SUCCESS", drafts);
    }
}
