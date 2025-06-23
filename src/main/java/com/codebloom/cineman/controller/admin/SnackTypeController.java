package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.SnackTypeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SnackTypeService;
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
@RequestMapping("${api.path}/admin/snack-types")
@Validated
@RequiredArgsConstructor
@Tag(name = "Snack Type Controller")
public class SnackTypeController {

    private final SnackTypeService snackTypeService;

    @Operation(summary = "Lấy tất cả loại snack")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSnackTypes() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy tất cả loại snack thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackTypeService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Lấy một loại snack theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSnackTypeById(@PathVariable @Min(1) int id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy thông tin loại snack thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackTypeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Tạo một loại snack")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createSnackType(@Valid @RequestBody SnackTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Tạo loại snack thành công.")
                        .status(HttpStatus.CREATED.value())
                        .data(snackTypeService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật một loại snack")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable @Min(1) Integer id,
                                              @Valid @RequestBody SnackTypeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Cập nhật loại snack thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackTypeService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Xóa một loại snack")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable @Min(1) Integer id) {
        snackTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Xóa loại snack thành công.")
                        .status(HttpStatus.OK.value())
                        .data(id)
                        .build()
        );
    }
}


