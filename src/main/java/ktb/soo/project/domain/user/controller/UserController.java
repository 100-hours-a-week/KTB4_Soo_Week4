package ktb.soo.project.domain.user.controller;


import jakarta.validation.Valid;
import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.UserResponse;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.service.UserService;
import ktb.soo.project.global.response.ApiResponse;
import ktb.soo.project.global.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        UserResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.of("USER_FETCH_SUCCESS", response));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserUpdateRequest request) {

        Long userId = userDetails.getUser().getId();
        userService.updateNickname(userId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PasswordUpdateRequest request) {

        Long userId = userDetails.getUser().getId();
        userService.updatePassword(userId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
