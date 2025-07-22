package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.PermissionRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.PermissionService;
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
@RequestMapping("${api.path}/admin/permissions")
@Validated
@RequiredArgsConstructor
@Tag(name = "Permission Controller", description = "Quản lý quyền truy cập các api của hệ thống")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Lấy tất cả permissions")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllPermissions() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy tất cả quyền thành công.")
                        .status(HttpStatus.OK.value())
                        .data(permissionService.getAll())
                        .build()
        );
    }

    @Operation(summary = "Lấy permission theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPermissionById(
            @PathVariable @Min(value = 1, message = "Permission ID phải >= 1") Integer id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Thành công.")
                        .status(HttpStatus.OK.value())
                        .data(permissionService.getById(id))
                        .build()
        );
    }

    @Operation(summary = "Tạo mới permission")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Tạo quyền mới thành công.")
                        .status(HttpStatus.CREATED.value())
                        .data(permissionService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật permission theo ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePermission(
            @PathVariable @Min(value = 1, message = "Permission ID phải >= 1") Integer id,
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Cập nhật quyền thành công.")
                        .status(HttpStatus.OK.value())
                        .data(permissionService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Xóa permission theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePermission(
            @PathVariable @Min(value = 1, message = "Permission ID phải >= 1") Integer id) {
        permissionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.builder()
                        .message("Xóa quyền thành công.")
                        .status(HttpStatus.NO_CONTENT.value())
                        .data(id)
                        .build()
        );
    }
}

