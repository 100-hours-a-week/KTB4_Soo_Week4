package ktb.soo.project.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 프로젝트의 모든 API 경로에 적용
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500") // 내 Live Server 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키나 인증 정보 포함 허용 시 필요
    }

}
