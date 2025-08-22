package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.UserRoleRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.UserRolePageableResponse;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.UserRoleResponse;
import com.codebloom.cineman.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/admin/user-role")
@Tag(name = "User-Role Controller", description = "Quản lý quyền hạn người dùng")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(summary = "Thêm user-role", description = "API thêm quyền hạn người dùng")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> create(@RequestBody UserRoleRequest request) {
        UserRoleResponse data = userRoleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật user-role", description = "API cập nhât quyền hạn người dùng")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody UserRoleRequest request) {
        UserRoleResponse data = userRoleService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }

    @Operation(summary = "Xóa user-role", description = "API xóa quyền hạn người dùng")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        userRoleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Lấy tất cả user-role", description = "API trả về tất cả các cặp user-role")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAllByPage(PageRequest request) {
        UserRolePageableResponse data = userRoleService.findAllByPage(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }
}

