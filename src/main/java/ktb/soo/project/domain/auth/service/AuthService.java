package ktb.soo.project.domain.auth.service;

import ktb.soo.project.domain.user.dto.LoginRequest;
import ktb.soo.project.domain.user.dto.SignUpRequest;
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
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 사용 중인 이메일 주소입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException("DUPLICATE_NICKNAME", HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.");
        }

        User newUser = new User(request.getEmail(), request.getPassword(), request.getNickname());
        userRepository.save(newUser);
    }

    public User login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", HttpStatus.BAD_REQUEST, "가입되지 않은 회원입니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("LOGIN_FAILED", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
