package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.RolePermissionRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/admin/role-permission")
@Tag(name = "Role-Permission Controller", description = "Quản lý quyền theo vai trò")
@RequiredArgsConstructor
public class RolePermissionAController {

    private final RolePermissionService rolePermissionService;

    @Operation(summary = "Gán quyền cho vai trò", description = "API dùng để gán một quyền cụ thể cho một vai trò")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPermissionToRole(@RequestBody @Valid RolePermissionRequest request) {
        rolePermissionService.addPermissionToRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Gán quyền thành công")
                        .data(request)
                        .build()
        );
    }

    @Operation(summary = "Gỡ quyền khỏi vai trò", description = "API dùng để xoá một quyền khỏi một vai trò")
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> removePermissionFromRole(@RequestBody @Valid RolePermissionRequest request) {
        rolePermissionService.removePermissionFromRole(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Gỡ quyền thành công")
                        .data(request)
                        .build()
        );
    }
}
