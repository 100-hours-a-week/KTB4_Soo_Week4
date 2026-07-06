package ktb.soo.project.global.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException{
    public UnauthorizedException(String code, String message) {
        super(code, HttpStatus.UNAUTHORIZED, message);
    }
}
