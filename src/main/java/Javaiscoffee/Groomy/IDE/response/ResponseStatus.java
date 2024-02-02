package Javaiscoffee.Groomy.IDE.response;

public enum ResponseStatus {
    SUCCESS("200", "성공"),
    REGISTER_FAILED("400", "회원가입에 실패하였습니다."),
    REGISTER_DUPLICATED("401","중복된 이메일이 존재합니다."),
    PASSWORD_INCORRECT("402","비밀번호가 틀렸습니다."),
    NOT_FOUND("404", "찾을 수 없음"),
    ERROR("500", "서버 오류");
    // 기타 상태 코드

    private final String code;
    private final String message;

    ResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
