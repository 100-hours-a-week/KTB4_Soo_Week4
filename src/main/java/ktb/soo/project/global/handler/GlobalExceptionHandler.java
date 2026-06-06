package ktb.soo.project.global.handler;

import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.exception.NotFoundException;
import ktb.soo.project.global.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Void> handleNotFound(
            NotFoundException exception) {

        return ApiResponse.of(exception.getCode(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(
            BusinessException exception) {

        return ApiResponse.of(exception.getCode(), null);
    }
}
