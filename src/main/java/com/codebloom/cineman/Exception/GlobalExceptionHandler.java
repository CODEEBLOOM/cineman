package com.codebloom.cineman.Exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice // đây là anotation để chỉ định  xu li ngoai le chung
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 404);
        error.put("message", "Sever không tìm thấy " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 500);
        error.put("message", "Lỗi hệ thống: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
