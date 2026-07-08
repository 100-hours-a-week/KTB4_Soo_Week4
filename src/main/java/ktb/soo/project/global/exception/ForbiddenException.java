package ktb.soo.project.global.exception;

public class ForbiddenException extends BusinessException{
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
