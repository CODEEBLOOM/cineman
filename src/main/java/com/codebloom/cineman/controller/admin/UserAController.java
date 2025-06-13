package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.ChangePasswordRequest;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
import com.codebloom.cineman.controller.response.UserPaginationResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller ( User api )")
@RequiredArgsConstructor
public class UserAController {

    private final UserService userService;
    Map<String, Object> response;

    @Operation(summary = "Get all users", description = "API dùng để lấy ra toàn bộ users có trong hệ thống.")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestBody PageQueryRequest request) {
        UserPaginationResponse userList = userService.findAll(request);
        response = new LinkedHashMap<>();
        response.put("data", userList);
        response.put("message", "User List");
        response.put("status", HttpStatus.OK.value());
        return response;
    }

    @Operation(summary = "Find User By User Id", description = "API dùng để lấy ra user theo user_id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable @Min(1) Long userId) {
        UserResponse userRes = userService.findById(userId);
        response = new LinkedHashMap<>();
        response.put("data", userRes);
        response.put("message", "User List");
        response.put("status", HttpStatus.OK.value());
        return response;
    }

    @Operation(summary = "Create New User", description = "API dùng để tạo mới một User")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody @Valid UserCreationRequest request) {
        Long userId = userService.save(request);
        response = new LinkedHashMap<>();
        response.put("status", CREATED.value());
        response.put("message", "User Created Success");
        response.put("data", userId);
        return ResponseEntity.status(CREATED).body(response);
    }

    @Operation(summary = "Update User", description = "API dùng để cập nhật một User")
    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody UserUpdateRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.ACCEPTED.value());
        response.put("message", "User Updated Success");
        response.put("data", "");
        return response;
    }

    @Operation(summary = "Change Password", description = "API dùng để thay đổi mật khẩu của User")
    @PutMapping("/change-pwd")
    public Map<String, Object> changePassword(@RequestBody ChangePasswordRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NO_CONTENT.value());
        response.put("message", "Change Password Success");
        response.put("data", "");
        return response;
    }

    @Operation(summary = "Disable User", description = "API dùng để chuyển isActive thành false")
    @DeleteMapping("/{userId}/del")
    public Map<String, Object> delete(@PathVariable Long userId) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NO_CONTENT.value());
        response.put("message", "Disable User Success");
        response.put("data", "");
        return response;
    }

}
