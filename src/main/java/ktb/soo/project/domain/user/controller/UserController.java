package ktb.soo.project.domain.user.controller;


import jakarta.validation.Valid;
import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.service.UserService;
import ktb.soo.project.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(
            @LoginUser Long userId,
            @RequestBody @Valid UserUpdateRequest request) {

        userService.updateNickname(userId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @LoginUser Long userId,
            @RequestBody @Valid PasswordUpdateRequest request) {

        userService.updatePassword(userId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
