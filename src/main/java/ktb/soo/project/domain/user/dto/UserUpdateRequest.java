package ktb.soo.project.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    @NotBlank(message = "NICKNAME_EMPTY")
    @Size(max = 10, message = "INVALID_NICKNAME_LENGTH")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "NICKNAME_CONTAINS_SPACE"
    )
    private String newNickname;

    public UserUpdateRequest(String newNickname){
        this.newNickname = newNickname;
    }
}
