package ktb.soo.project.domain.user.service;

import ktb.soo.project.domain.user.dto.SignUpRequest;
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

    public void signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("DUPLICATE_EMAIL", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException("DUPLICATE_NICKNAME", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User(request.getEmail(), request.getPassword(), request.getNickname());
        userRepository.save(newUser);
    }

}
