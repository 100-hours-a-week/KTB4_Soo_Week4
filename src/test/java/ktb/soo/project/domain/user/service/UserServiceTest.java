package ktb.soo.project.domain.user.service;

import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.UserResponse;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        // 모든 테스트에서 공통으로 사용할 가짜 유저 객체 미리 생성
        testUser = new User("test@gmail.com", "encodedCurrentPw", "soo");
    }

    // ==========================================
    // 1. 내 정보 조회 테스트 (시나리오 1, 2)
    // ==========================================
    @Test
    @DisplayName("성공: 유효한 ID로 조회 시 내 정보(UserResponse)를 반환한다")
    void getUserProfileSuccess() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));

        // when
        UserResponse response = userService.getUserProfile(userId);

        // then
        assertEquals("test@gmail.com", response.getEmail());
        assertEquals("soo", response.getNickname());
    }

    @Test
    @DisplayName("조회 실패: 존재하지 않는 유저 ID로 조회 시 USER_NOT_FOUND 예외가 발생한다")
    void getUserProfileFailUserNotFound() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.getUserProfile(userId)
        );
        assertEquals("USER_NOT_FOUND", exception.getCode());
    }

    // ==========================================
    // 2. 닉네임 수정 테스트 (시나리오 1, 2, 3)
    // ==========================================
    @Test
    @DisplayName("닉네임 수정 성공: 유효한 ID와 중복 없는 닉네임이면 성공한다")
    void updateNicknameSuccess() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("새로운닉네임");

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(userRepository.existsByNicknameAndIdNot("새로운닉네임", userId)).willReturn(false);

        // when
        userService.updateNickname(userId, request);

        // then
        assertEquals("새로운닉네임", testUser.getNickname());
    }

    @Test
    @DisplayName("닉네임 수정 실패: 존재하지 않는 유저 ID로 수정 시 예외가 발생한다")
    void updateNicknameFailUserNotFound() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("새로운닉네임");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.updateNickname(userId, request)
        );
        assertEquals("USER_NOT_FOUND", exception.getCode());
    }

    @Test
    @DisplayName("실패: 닉네임 수정 시 타인이 이미 사용 중인 닉네임이면 예외가 발생한다")
    void updateNicknameFailDuplicate() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("중복닉네임");

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(userRepository.existsByNicknameAndIdNot("중복닉네임", userId)).willReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.updateNickname(userId, request)
        );

        assertEquals("DUPLICATE_NICKNAME", exception.getCode());
    }

    // ==========================================
    // 3. 비밀번호 변경 테스트 (시나리오 1, 2, 3, 4)
    // ==========================================
    @Test
    @DisplayName("비밀번호 변경 성공: 모든 조건이 올바르면 암호화하여 정상 변경한다")
    void updatePasswordSuccess() {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("correctCurrentPw", "newPw123!", "newPw123!");
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("correctCurrentPw", "encodedCurrentPw")).willReturn(true);
        given(passwordEncoder.encode("newPw123!")).willReturn("encodedNewPw123!");

        // when
        userService.updatePassword(userId, request);

        // then - 비번이 새로 암호화된 값으로 잘 바뀌었는지 검증
        assertEquals("encodedNewPw123!", testUser.getPassword());
    }

    @Test
    @DisplayName("비밀번호 변경 실패: 존재하지 않는 유저 ID로 변경 시 예외가 발생한다")
    void updatePasswordFailUserNotFound() {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("pw", "new", "new");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.updatePassword(userId, request)
        );
        assertEquals("USER_NOT_FOUND", exception.getCode());
    }

    @Test
    @DisplayName("실패: 비밀번호 변경 시 현재 비밀번호가 틀리면 예외가 발생한다")
    void updatePasswordFailCurrentPasswordMismatch() {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("wrongCurrentPw", "newPw123!", "newPw123!");

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("wrongCurrentPw", "encodedCurrentPw")).willReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.updatePassword(userId, request)
        );

        assertEquals("INVALID_CURRENT_PASSWORD", exception.getCode());
    }

    @Test
    @DisplayName("실패: 비밀번호 변경 시 새 비밀번호 확인이 일치하지 않으면 예외가 발생한다")
    void updatePasswordFailConfirmMismatch() {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("correctCurrentPw", "newPw123!", "differentPw123!");

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("correctCurrentPw", "encodedCurrentPw")).willReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.updatePassword(userId, request)
        );

        assertEquals("PASSWORD_CONFIRM_MISMATCH", exception.getCode());
    }

}