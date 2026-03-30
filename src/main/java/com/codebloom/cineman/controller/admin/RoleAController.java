package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.RoleRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/admin/role")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role Controller", description = "Quan ly role dong va trang thai role")
public class RoleAController {

    private final RoleService roleService;

    @Operation(summary = "Get all roles")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllRoles() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Get roles successfully")
                        .data(roleService.getAllRoles())
                        .build()
        );
    }

    @Operation(summary = "Get role by id")
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable @NotBlank String roleId) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Get role successfully")
                        .data(roleService.getRoleById(roleId))
                        .build()
        );
    }

    @Operation(summary = "Create role")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create role successfully")
                        .data(roleService.createRole(request))
                        .build()
        );
    }

    @Operation(summary = "Update role")
    @PutMapping("/{roleId}/update")
    public ResponseEntity<ApiResponse> updateRole(
            @PathVariable @NotBlank String roleId,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update role successfully")
                        .data(roleService.updateRole(roleId, request))
                        .build()
        );
    }

    @Operation(summary = "Change role status")
    @PatchMapping("/{roleId}/status")
    public ResponseEntity<ApiResponse> changeStatus(
            @PathVariable @NotBlank String roleId,
            @RequestParam boolean status) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Change role status successfully")
                        .data(roleService.changeStatus(roleId, status))
                        .build()
        );
    }

    @Operation(summary = "Delete role")
    @DeleteMapping("/{roleId}/delete")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable @NotBlank String roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete role successfully")
                        .data(null)
                        .build()
        );
    }
}
