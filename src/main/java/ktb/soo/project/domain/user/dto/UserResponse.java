package ktb.soo.project.domain.user.dto;

import lombok.Getter;

@Getter
public class UserResponse {
    private String email;
    private String nickname;

    public UserResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}

