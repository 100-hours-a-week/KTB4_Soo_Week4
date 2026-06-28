package ktb.soo.project.domain.user.service;

import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.UserResponse;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void updateNickname(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        if (userRepository.existsByNickname(request.getNewNickname())) {
            throw new BusinessException("DUPLICATE_NICKNAME", HttpStatus.CONFLICT, "이미 다른 사용자가 사용 중인 닉네임입니다.");
        }

        user.updateNickname(request.getNewNickname());
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        user.updatePassword(request.getNewPassword());
    }


    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        return new UserResponse(user.getEmail(), user.getNickname());
    }
}
