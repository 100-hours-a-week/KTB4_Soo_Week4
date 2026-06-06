package ktb.soo.project.global.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException{
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
