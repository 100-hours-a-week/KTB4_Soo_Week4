package ktb.soo.project.domain.auth.service;

import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("성공 : 중복되지 않은 정보로 회원가입 시 성공한다.")
    void signUpSuccess() {
        // given
        SignUpRequest request = new SignUpRequest("test@gamil.com","Password123!", "soo");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByNickname(request.getNickname())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encoded_pw");

        // when
        authService.signUp(request);

        // then
       verify(userRepository, times(1)).save(any(User.class));


    }

    @Test
    @DisplayName("실패 : 이미 존재하는 이메일로 가입을 시도하면 예외가 발생한다")
    void signUpFailDuplicateEmail() {
        // given
        SignUpRequest request = new SignUpRequest("test@gamil.com", "Password123!", "soo");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.signUp(request)
        );

        // then
        assertEquals(ErrorCode.DUPLICATE_EMAIL.name(), exception.getCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패 : 이미 존재하는 닉네임으로 가입을 시도하면 예외가 발생한다")
    void signUpFailDuplicateNickname() {
        // given
        SignUpRequest request = new SignUpRequest("test@gmail.com", "Password123!", "soo");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByNickname(request.getNickname())).willReturn(true);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.signUp(request)
        );

        // then
        assertEquals(ErrorCode.DUPLICATE_NICKNAME.name(), exception.getCode());
        verify(userRepository, never()).save(any(User.class));
    }


}
