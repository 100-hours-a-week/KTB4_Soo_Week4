package ktb.soo.project.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "EMAIL_EMPTY")
    @Email(message = "INVALID_EMAIL_FORMAT")
    private String email;

    @NotBlank(message = "PASSWORD_EMPTY")
    @Size(min = 8, max = 20, message = "INVALID_PASSWORD_LENGTH")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "INVALID_PASSWORD_COMPLEXITY"
    )
    private String password;

    @NotBlank(message = "NICKNAME_EMPTY")
    @Size(max = 10, message = "INVALID_NICKNAME_LENGTH")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "NICKNAME_CONTAINS_SPACE"
    )
    private String nickname;

    public SignUpRequest(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
