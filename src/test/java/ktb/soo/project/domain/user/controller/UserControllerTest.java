package ktb.soo.project.domain.user.controller;

import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.UserResponse;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.service.UserService;
import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.security.handler.JwtAccessDeniedHandler;
import ktb.soo.project.global.security.handler.JwtAuthenticationEntryPoint;
import ktb.soo.project.global.security.principal.CustomUserDetails;
import ktb.soo.project.global.security.provider.JwtTokenProvider;
import ktb.soo.project.global.security.token.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // 시큐리티 설정 및 JPA 메타데이터 에러 방지용 가짜 빈 등록
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockitoBean private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @MockitoBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    // ==========================================
    // 1. 내 정보 조회 테스트
    // ==========================================

    @Test
    @DisplayName("조회 성공: 로그인한 유저가 내 정보 조회 시 200 OK와 회원 정보를 반환한다")
    void getMeSuccess() throws Exception {
        // given
        UserResponse response = new UserResponse("test@gmail.com", "soo");
        given(userService.getUserProfile(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USER_FETCH_SUCCESS"))
                .andExpect(jsonPath("$.data.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.data.nickname").value("soo"));
    }

    // ==========================================
    // 2. 닉네임 수정 테스트
    // ==========================================

    @Test
    @DisplayName("닉네임 수정 성공: 올바른 요청 시 204 No Content를 반환한다")
    void updateMeSuccess() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("새로운닉네임");
        willDoNothing().given(userService).updateNickname(any(), any(UserUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNoContent()); // 204 반환 검증
    }

    @Test
    @WithMockUser
    @DisplayName("닉네임 수정 실패: 변경할 닉네임이 공백이거나 규칙에 맞지 않는 @Valid 실패 요청 시 400 Bad Request를 반환한다")
    void updateMeFailInvalidNickname() throws Exception {
        // given
        UserUpdateRequest invalidRequest = new UserUpdateRequest("");

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // 400 에러 검증

        verify(userService, never()).updateNickname(any(), any());
    }

    @Test
    @DisplayName("닉네임 수정 실패: 타인이 사용 중인 닉네임이면 409 Conflict를 반환한다")
    void updateMeFailDuplicateNickname() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("중복된닉네임");

        // 서비스에서 BusinessException(DUPLICATE_NICKNAME)을 던지도록 설정
        willThrow(new BusinessException("DUPLICATE_NICKNAME", HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."))
                .given(userService).updateNickname(any(), any(UserUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict()) // 409 검증
                .andExpect(jsonPath("$.code").value("DUPLICATE_NICKNAME"));
    }

    // ==========================================
    // 3. 비밀번호 변경 테스트
    // ==========================================

    @Test
    @DisplayName("비밀번호 변경 성공: 올바른 요청 시 204 No Content를 반환한다")
    void updatePasswordSuccess() throws Exception {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("currentPw123!", "newPw123!", "newPw123!");
        willDoNothing().given(userService).updatePassword(any(), any(PasswordUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("비밀번호 변경 실패: 입력 조건이 누락되거나 틀린 @Valid 실패 요청 시 400 Bad Request를 반환한다")
    void updatePasswordFailInvalidDto() throws Exception {
        // given
        PasswordUpdateRequest invalidRequest = new PasswordUpdateRequest("", "short", "");

        // when & then
        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // 400 에러 검증!

        verify(userService, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("비밀번호 변경 실패: 현재 비밀번호가 틀리면 401 Unauthorized를 반환한다")
    void updatePasswordFailCurrentPasswordMismatch() throws Exception {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("wrongPw", "newPw123!", "newPw123!");

        willThrow(new BusinessException("INVALID_CURRENT_PASSWORD", HttpStatus.UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다."))
                .given(userService).updatePassword(any(), any(PasswordUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized()) // 401 검증
                .andExpect(jsonPath("$.code").value("INVALID_CURRENT_PASSWORD"));
    }

    @TestConfiguration
    static class TestWebMvcConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.getParameterType().equals(CustomUserDetails.class);
                }

                @Override
                public Object resolveArgument(MethodParameter parameter,
                                              ModelAndViewContainer mavContainer,
                                              NativeWebRequest webRequest,
                                              WebDataBinderFactory binderFactory) {
                    return new CustomUserDetails(new User("test@gmail.com", "Password123!", "soo"));
                }
            });
        }
    }

}
