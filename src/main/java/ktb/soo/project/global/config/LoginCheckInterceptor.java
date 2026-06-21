package ktb.soo.project.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ktb.soo.project.global.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method) && requestURI.startsWith("/api/v1/posts")) {
            return true;
        }


        // 세션이 없으면 로그인 안 한 상태
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER") == null) {
            throw new BusinessException("UNAUTHORIZED_USER", HttpStatus.UNAUTHORIZED);
        }

        return true; // 세션이 있으면 통과
    }
}
