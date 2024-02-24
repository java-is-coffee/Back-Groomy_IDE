package javaiscoffee.groomy.ide.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 추가해야 할 수도 있는 파라미터 관련 예외
 * 파라미터 입력값과 관련하여 발생할 수 있는 예외는 다음과 같습니다:
 *
 * BindException: 바인딩 실패시 발생하는 예외입니다. 예를 들어, 요청 파라미터를 모델에 바인딩하는 과정에서 타입 불일치 등으로 인해 발생할 수 있습니다.
 *
 * TypeMismatchException: 요청 파라미터의 타입이 메서드 파라미터 타입과 일치하지 않을 때 발생하는 예외입니다.
 *
 * ConversionNotSupportedException: 지정된 타입으로 변환할 수 없는 경우 발생하는 예외입니다.
 *
 * HttpMediaTypeNotSupportedException: 클라이언트가 요청에서 지원하지 않는 Content-Type을 사용한 경우 발생합니다.
 *
 * HttpMediaTypeNotAcceptableException: 클라이언트가 Accept 헤더를 통해 서버가 반환할 수 없는 미디어 타입을 요청한 경우 발생합니다.
 *
 * MissingServletRequestPartException: 멀티파트 요청에서 특정 파트가 누락되었을 때 발생합니다.
 *
 * ServletRequestBindingException: 일반적인 요청 바인딩 실패에 대한 예외입니다.
 *
 * MethodArgumentTypeMismatchException: 메서드 인자의 타입이 올바르지 않을 경우 발생하는 예외입니다.
 *
 * MissingPathVariableException: URI 템플릿 변수가 누락된 경우 발생하는 예외입니다.
 *
 * ConstraintViolationException: Bean Validation API를 사용할 때 제약 조건 위반시 발생하는 예외입니다.
 *
 * UnsatisfiedServletRequestParameterException: 요청 매핑 조건을 만족하지 못할 때 발생하는 예외입니다.
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Status> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 에러 메시지를 StringBuilder를 사용해 하나로 합치기
//        StringBuilder errors = new StringBuilder();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String errorMessage = error.getDefaultMessage();
//            errors.append(errorMessage).append(" ");
//        });

        return new ResponseEntity<>(new Status(ResponseStatus.INPUT_ERROR), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Status> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // 로그 메시지나 클라이언트에 반환할 메시지를 생성
//        String errorMessage = "파싱 오류: 요청 본문의 형식이 잘못되었습니다.";

        return new ResponseEntity<>(new Status(ResponseStatus.INPUT_ERROR), HttpStatus.BAD_REQUEST);
    }

    // 파라미터 값이 전부 안 왔을 때 처리하는 예외 핸들러
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Status> handleMissingParams(MissingServletRequestParameterException ex) {
//        // 에러 메시지 생성
//        String message = ex.getParameterName() + "값이 존재하지 않습니다.";

        return new ResponseEntity<>(new Status(ResponseStatus.INPUT_ERROR), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleMemberNotFoundException(BaseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseStatus.SEND_FAILED);
    }

    // 다른 예외 유형을 처리하는 핸들러를 추가할 수 있습니다.


}
