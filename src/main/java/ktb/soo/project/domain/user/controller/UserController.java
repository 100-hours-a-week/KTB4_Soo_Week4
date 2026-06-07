package ktb.soo.project.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ktb.soo.project.domain.user.dto.LoginRequest;
import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.service.UserService;
import ktb.soo.project.global.annotation.LoginUser;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/me")
    public ApiResponse<Void> updateMe(
            @LoginUser Long userId,
            @RequestBody @Valid UserUpdateRequest request) {

        userService.updateNickname(userId, request);
        return ApiResponse.of("UPDATE_SUCCESS", null);
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> updatePassword(
            @LoginUser Long userId,
            @RequestBody @Valid PasswordUpdateRequest request) {

        userService.updatePassword(userId, request);
        return ApiResponse.of("PASSWORD_UPDATE_SUCCESS", null);
    }

}
