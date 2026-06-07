package ktb.soo.project.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
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

    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }
}
