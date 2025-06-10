package com.codebloom.cineman.Exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    // xử lý NotFoundException (404)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, "Không tìm thấy: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // cái này là xử lỹ lỗi 400 ghi có dữ liệu nhập không hợp lệ mà chắc có validation r thì ko cần cái này cx đc
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("Dữ liệu không hợp lệ");

        ErrorResponse error = new ErrorResponse(400, "Lỗi validation: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // xử lý lỗi 400 khi dữ liệu không hợp lệ, ví dụ như khi dùng @Valid mà dữ liệu không hợp lệ
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("Dữ liệu không hợp lệ");

        ErrorResponse error = new ErrorResponse(400, "Lỗi validation: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

 // xử lý lỗi 400 khi tham số không hợp lệ, ví dụ như khi dùng @RequestParam mà giá trị không hợp lệ
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(400, "Tham số không hợp lệ: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // xử lý lỗi 500 cho các lỗi không xác định khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(500, "Lỗi hệ thống không xác định");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
