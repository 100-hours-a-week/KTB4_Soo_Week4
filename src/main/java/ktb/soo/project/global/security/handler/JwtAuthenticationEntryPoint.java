package ktb.soo.project.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktb.soo.project.global.exception.ErrorCode;
import ktb.soo.project.global.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static ktb.soo.project.global.security.filter.JwtAuthenticationFilter.JWT_EXCEPTION_ATTRIBUTE;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ErrorCode errorCode = (ErrorCode) request.getAttribute(JWT_EXCEPTION_ATTRIBUTE);

        // 토큰이 없거나 필터에서 전달된 JWT 오류가 없는 경우 기본 오류 사용
        if (errorCode == null) {
            errorCode = ErrorCode.INVALID_TOKEN;
        }

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ApiResponse 포맷에 맞게 객체 생성
        ApiResponse<Void> apiResponse = ApiResponse.onFailure(
                errorCode.name(),
                errorCode.getMessage()
        );

        // ObjectMapper를 이용해 객체를 JSON 문자열로 변환 후 응답 스트림에 꽂아 넣기
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
