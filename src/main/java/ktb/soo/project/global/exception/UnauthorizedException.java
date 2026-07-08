package ktb.soo.project.global.exception;

public class UnauthorizedException extends BusinessException{
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
