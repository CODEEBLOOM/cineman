package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SnackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/admin/snacks")
@Validated
@RequiredArgsConstructor
@Tag(name = "Snack Controller")

public class SnackAController {

    private final SnackService snackService;

    @Operation(summary = "Lấy tất cả snack")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSnacks() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy tất cả snack thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Lấy một snack theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSnackById(@PathVariable @Min(value = 1, message = "Id's snack is must be greater than or equal 1 !") Integer id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Success.")
                        .status(HttpStatus.OK.value())
                        .data(snackService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Tạo một snack")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createSnack(@Valid @RequestBody SnackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Create snack success.")
                        .status(HttpStatus.CREATED.value())
                        .data(snackService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật một snack")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSnack(@PathVariable@Min(value = 1, message = "Id's snack is must be greater than or equal 1 !") Integer id,
                                                   @Valid @RequestBody SnackRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Cập nhật snack thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Xóa một snack")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSnack(@PathVariable @Min(value = 1, message = "Id's snack is must be greater than or equal 1 !") Integer id) {
        snackService.delete(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.builder()
                        .message("Xóa snack thành công.")
                        .status(HttpStatus.NO_CONTENT.value())
                        .data(id)
                        .build()
        );
    }
}

