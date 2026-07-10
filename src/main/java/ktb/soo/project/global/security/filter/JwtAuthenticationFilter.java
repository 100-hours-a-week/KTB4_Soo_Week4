package ktb.soo.project.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktb.soo.project.global.exception.UnauthorizedException;
import ktb.soo.project.global.security.provider.JwtTokenProvider;
import ktb.soo.project.global.security.token.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String JWT_EXCEPTION_ATTRIBUTE = "jwtException";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // HTTP 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 토큰이 존재하고, 유효성 검증을 통과하면 시큐리티에 인증 객체 등록
        if (StringUtils.hasText(token)) {
            try {
                jwtTokenProvider.validateToken(token); // 토큰 검증 (만료, 변조 체크)

                String email = jwtTokenProvider.getEmail(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UnauthorizedException e) {
                // 토큰 검증 중 예외가 발생하면 시큐리티 컨텍스트를 비워두고 그냥 다음 필터로 넘김
                // (이후 SecurityConfig 설정에 따라 인가 처리가 차단됨)
                SecurityContextHolder.clearContext();
                request.setAttribute(JWT_EXCEPTION_ATTRIBUTE, e.getErrorCode());
            }
        }

        // 다음 필터 체인으로 요청 넘기기
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더에서 "Authorization: Bearer <토큰문자열>" 형태의 토큰을 파싱하는 유틸 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
