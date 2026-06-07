package ktb.soo.project.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ktb.soo.project.domain.user.dto.LoginRequest;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.service.UserService;
import ktb.soo.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ApiResponse<Void> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest servletRequest) { // 세션을 쓰기 위해 서블릿 리퀘스트를 주입받음

        User loginUser = userService.login(request);

        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("LOGIN_USER", loginUser.getId());

        return ApiResponse.of("LOGIN_SUCCESS", null);
    }

}
