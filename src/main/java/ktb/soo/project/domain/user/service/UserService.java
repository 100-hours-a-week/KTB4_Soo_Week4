package ktb.soo.project.domain.user.service;

import ktb.soo.project.domain.user.dto.LoginRequest;
import ktb.soo.project.domain.user.dto.PasswordUpdateRequest;
import ktb.soo.project.domain.user.dto.SignUpRequest;
import ktb.soo.project.domain.user.dto.UserUpdateRequest;
import ktb.soo.project.domain.user.entity.User;
import ktb.soo.project.domain.user.repository.UserRepository;
import ktb.soo.project.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void updateNickname(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (userRepository.existsByNickname(request.getNewNickname())) {
            throw new BusinessException("DUPLICATE_NICKNAME", HttpStatus.CONFLICT);
        }

        user.updateNickname(request.getNewNickname());
    }

    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        user.updatePassword(request.getNewPassword());
    }



}
