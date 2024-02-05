package javaiscoffee.groomy.ide.response;

public enum ResponseStatus {
    SUCCESS(200, "성공"),
    REGISTER_DUPLICATED(301,"중복된 이메일이 존재합니다."),
    LOGIN_FAILED(302,"로그인에 실패했습니다."),
    INPUT_ERROR(303, "입력값이 잘못되었습니다."),
    REGISTER_FAILED(400, "회원가입에 실패하였습니다."),
    UNAUTHORIZED(401,"유효하지 않은 토큰입니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    NOT_FOUND(404, "찾을 수 없음"),
    ERROR(500, "서버 오류");
    // 기타 상태 코드

    private final Integer code;
    private final String message;

    ResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}