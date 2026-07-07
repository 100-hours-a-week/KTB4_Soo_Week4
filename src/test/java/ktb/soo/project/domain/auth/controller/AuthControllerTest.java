package ktb.soo.project.domain.auth.controller;


import ktb.soo.project.domain.auth.service.AuthService;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.security.handler.JwtAccessDeniedHandler;
import ktb.soo.project.global.security.handler.JwtAuthenticationEntryPoint;
import ktb.soo.project.global.security.provider.JwtTokenProvider;
import ktb.soo.project.global.security.token.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUser // 시큐리티 인증 필터를 통과하기 위한 가짜 유저 권한 부여
    @DisplayName("성공 : 올바른 데이터로 회원가입 요청 시 201 Created를 반환한다")
    void signUpSuccess() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("test@gmail.com", "Password123!", "지수닉");

        // 서비스의 signUp 메서드는 리턴 타입이 void이므로 실행 시 아무 일도 안 일어나야한다.
        willDoNothing().given(authService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SIGNUP_SUCCESS"));


    }

    @Test
    @WithMockUser
    @DisplayName("실패 : 이메일 형식이 올바르지 않으면 400 Bad Request를 반환한다")
    void signUpFailInvalidEmail() throws Exception {
        SignUpRequest invalidRequest = new SignUpRequest("invalidEmail", "Password123!", "soo");

        // when & then: 서비스까지 가지도 못하고 컨트롤러 진입 단계에서 400(Bad Request)에러가 발생한다.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("실패 : 이미 존재하는 이메일로 가입 시 409 Conflict를 반환한다")
    void signUpFailDuplicateEmail() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("duplicate@gmail.com", "Password123!", "soo");

        willThrow(new BusinessException(
                "DUPLICATE_EMAIL",
                HttpStatus.CONFLICT,
                "이미 사용 중인 이메일입니다."
        )).given(authService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }
}
