가package ktb.soo.project.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일 주소입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    DRAFT_NOT_FOUND(HttpStatus.NOT_FOUND, "임시저장 글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "답글을 달려는 원댓글이 존재하지 않습니다."),

    INVALID_COMMENT_POST_MISMATCH(HttpStatus.BAD_REQUEST, "타 게시글의 댓글에는 답글을 달 수 없습니다."),
    INVALID_CURRENT_PASSWORD(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "새 비밀번호 확인이 일치하지 않습니다."),
    ALREADY_DELETED_COMMENT(HttpStatus.BAD_REQUEST, "이미 삭제된 댓글입니다."),

    UNAUTHORIZED_POST_ACCESS(HttpStatus.FORBIDDEN, "해당 글에 대한 권한이 없습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 접근할 수 있습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "요청하신 JSON 데이터 포맷이 올바르지 않거나 파싱할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 관리자에게 문의해 주세요.");

    private final HttpStatus status;
    private final String message;
}
