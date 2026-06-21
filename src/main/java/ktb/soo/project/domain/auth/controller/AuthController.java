package ktb.soo.project.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ktb.soo.project.domain.auth.service.AuthService;
import ktb.soo.project.domain.user.dto.LoginRequest;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.domain.user.entity.User;
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
    public ResponseEntity<ApiResponse<Void>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest servletRequest) { // 세션을 쓰기 위해 서블릿 리퀘스트를 주입받음

        User loginUser = authService.login(request);

        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("LOGIN_USER", loginUser.getId());

        return ResponseEntity.ok(ApiResponse.of("LOGIN_SUCCESS", null));
    }
}
