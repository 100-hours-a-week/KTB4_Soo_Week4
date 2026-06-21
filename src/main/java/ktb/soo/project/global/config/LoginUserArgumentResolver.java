package ktb.soo.project.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ktb.soo.project.global.annotation.LoginUser;
import ktb.soo.project.global.exception.BusinessException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    // 컨트롤러 매개변수에 @LoginUser Long userId 가 붙어있는지 확인하는 역할
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isLongType = Long.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && isLongType;
    }

    // supportsParameter가 true를 반환하면 실제로 세션에서 값을 꺼내 배달해주는 역할
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new BusinessException("UNAUTHORIZED_ACCESS", HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다.");
        }

        Long userId = (Long) session.getAttribute("LOGIN_USER");

        if (userId == null) {
            throw new BusinessException("UNAUTHORIZED_ACCESS", HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 정보입니다.");
        }

        return userId;
    }
}
