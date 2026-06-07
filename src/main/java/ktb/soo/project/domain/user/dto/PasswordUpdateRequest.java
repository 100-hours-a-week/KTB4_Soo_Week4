package ktb.soo.project.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordUpdateRequest {
    @NotBlank(message = "PASSWORD_EMPTY")
    @Size(min = 8, max = 20, message = "INVALID_PASSWORD_LENGTH")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "INVALID_PASSWORD_COMPLEXITY"
    )
    private String newPassword;

    public PasswordUpdateRequest(String newPassword) {
        this.newPassword = newPassword;
    }
}
