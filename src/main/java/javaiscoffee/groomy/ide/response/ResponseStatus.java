package javaiscoffee.groomy.ide.response;

public enum ResponseStatus {
    SUCCESS("성공"),
    REGISTER_DUPLICATED("중복된 이메일이 존재합니다."),
    LOGIN_FAILED("로그인에 실패했습니다."),
    INPUT_ERROR("입력값이 잘못되었습니다."),
    REGISTER_FAILED("회원가입에 실패했습니다."),
    UNAUTHORIZED("유효하지 않은 토큰입니다."),
    SAVE_FAILED("저장에 실패했습니다."),
    DELETE_FAILED("삭제에 실패했습니다."),
    READ_FAILED("조회에 실패했습니다."),
    FORBIDDEN("권한이 없습니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    NOT_FOUND("찾을 수 없음"),
    SEND_FAILED("메세지를 전달할 수 없습니다."),
    ERROR("서버 오류");
    // 기타 상태 코드

    private final String message;

    ResponseStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
