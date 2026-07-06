package ktb.soo.project.domain.auth.controller;

import jakarta.validation.Valid;
import ktb.soo.project.domain.auth.dto.TokenResponse;
import ktb.soo.project.domain.auth.service.AuthService;
import ktb.soo.project.domain.user.dto.LoginRequest;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid SignUpRequest request) {

        authService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("SIGNUP_SUCCESS", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @RequestBody @Valid LoginRequest request
            ) {

        TokenResponse tokenResponse = authService.login(request);

        return ResponseEntity.ok(ApiResponse.of("LOGIN_SUCCESS", tokenResponse));
    }
}
