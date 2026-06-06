package ktb.soo.project.domain.user.controller;

import jakarta.validation.Valid;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.domain.user.service.UserService;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<Void> signUp(@RequestBody @Valid SignUpRequest request) {

        userService.signUp(request);

        return ApiResponse.of("SIGNUP_SUCCESS", null);
    }

}
