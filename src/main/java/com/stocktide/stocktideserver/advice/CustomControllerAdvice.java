package com.stocktide.stocktideserver.advice;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class CustomControllerAdvice {

    // 없는 URL
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notExist(NoSuchElementException e) {
        // NOT_FOUND : 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", e.getMessage()));
    }

    // 매개변수의 타입 잘못
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // NOT_ACCEPTABLE : 406
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("msg", e.getMessage()));
    }

    // 제약 조건 위반
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        // BAD_REQUEST : 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", e.getMessage()));
    }

    // 비즈니스 로직 예외 처리
//    @ExceptionHandler(BusinessLogicException.class)
//    public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException e) {
//        final ErrorResponse response = ErrorResponse.of(e.getExceptionCode());
//        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getExceptionCode().getStatus()));
//    }
}
