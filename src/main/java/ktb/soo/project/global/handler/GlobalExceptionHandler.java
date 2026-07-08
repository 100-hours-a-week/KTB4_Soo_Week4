package ktb.soo.project.global.handler;

import ktb.soo.project.global.exception.BusinessException;
import ktb.soo.project.global.exception.ErrorCode;
import ktb.soo.project.global.exception.NotFoundException;
import ktb.soo.project.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            NotFoundException exception) {

        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.onFailure(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            BusinessException exception) {
        String message = exception.getMessage() != null ? exception.getMessage() : "비즈니스 로직 처리 중 오류가 발생했습니다.";

        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.onFailure(exception.getCode(), message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException exception) {

        return ResponseEntity
                .status(ErrorCode.INVALID_LOGIN.getStatus())
                .body(ApiResponse.onFailure(ErrorCode.INVALID_LOGIN.name(), ErrorCode.INVALID_LOGIN.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {

        String errorCode = ErrorCode.INVALID_INPUT_VALUE.name();

        if(exception.getBindingResult().getFieldError() != null){
            errorCode = exception.getBindingResult().getFieldError().getDefaultMessage();
        }

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(errorCode, ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {

        return ResponseEntity
                .status(ErrorCode.INVALID_JSON_FORMAT.getStatus())
                .body(ApiResponse.onFailure(ErrorCode.INVALID_JSON_FORMAT.name(), ErrorCode.INVALID_JSON_FORMAT.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.name(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
