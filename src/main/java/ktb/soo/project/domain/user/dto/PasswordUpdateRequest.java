package ktb.soo.project.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequest {
    @NotBlank(message = "CURRENT_PASSWORD_EMPTY")
    private String currentPassword;

    @NotBlank(message = "PASSWORD_EMPTY")
    @Size(min = 8, max = 20, message = "INVALID_PASSWORD_LENGTH")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "INVALID_PASSWORD_COMPLEXITY"
    )
    private String newPassword;

    @NotBlank(message = "PASSWORD_CONFIRM_EMPTY")
    private String newPasswordConfirm;

    public PasswordUpdateRequest(String currentPassword, String newPassword, String newPasswordConfirm) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }
}
