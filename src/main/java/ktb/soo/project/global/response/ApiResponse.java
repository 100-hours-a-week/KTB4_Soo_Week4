package ktb.soo.project.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T>{
    private final String code;
    private final T data;

    public static <T> ApiResponse<T> of(String code, T data) {
        return new ApiResponse<>(code, data);
    }
}
