package ktb.soo.project.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException{
    public NotFoundException(String code) {
        super(code, HttpStatus.NOT_FOUND);
    }
}
