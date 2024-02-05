package javaiscoffee.groomy.ide.spring;

import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MyResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 에러 메시지를 StringBuilder를 사용해 하나로 합치기
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.append(errorMessage).append(" ");
        });

        // MyResponse 객체 생성 및 반환
        MyResponse<String> response = new MyResponse<>(new Status(ResponseStatus.INPUT_ERROR), errors.toString().trim());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MyResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // 로그 메시지나 클라이언트에 반환할 메시지를 생성
        String errorMessage = "파싱 오류: 요청 본문의 형식이 잘못되었습니다.";
        MyResponse<String> response = new MyResponse<>(new Status(ResponseStatus.INPUT_ERROR), errorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 다른 예외 유형을 처리하는 핸들러를 추가할 수 있습니다.
}
