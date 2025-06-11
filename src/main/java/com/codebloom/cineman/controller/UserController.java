package com.codebloom.cineman.controller;

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
@RequiredArgsConstructor // no giup minh khong can viet @Autowired
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "API dùng để lấy ra toàn bộ users có trong hệ thống.")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestBody PageQueryRequest request) {
        UserPaginationResponse userList = userService.findAll(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", userList);
        result.put("message", "User List");
        result.put("status", HttpStatus.OK.value());
        return result;
    }

    @Operation(summary = "Find User By User Id", description = "API dùng để lấy ra user theo user_id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable @Min(1) Long userId) {
        UserResponse userRes = userService.findById(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", userRes);
        result.put("message", "User List");
        result.put("status", HttpStatus.OK.value());
        return result;
    }

    @Operation(summary = "Create New User", description = "API dùng để tạo mới một User")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody @Valid UserCreationRequest request) {
        Long userId = userService.save(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", CREATED.value());
        result.put("message", "User Created Success");
        result.put("data", userId);
        return ResponseEntity.status(CREATED).body(result);
    }

    @Operation(summary = "Update User", description = "API dùng để cập nhật một User")
    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody UserUpdateRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User Updated Success");
        result.put("data", "");
        return result;
    }

    @Operation(summary = "Change Password", description = "API dùng để thay đổi mật khẩu của User")
    @PutMapping("/change-pwd")
    public Map<String, Object> changePassword(@RequestBody ChangePasswordRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Change Password Success");
        result.put("data", "");
        return result;
    }

    @Operation(summary = "Disable User", description = "API dùng để chuyển isActive thành false")
    @DeleteMapping("/{userId}/del")
    public Map<String, Object> delete(@PathVariable Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Disable User Success");
        result.put("data", "");
        return result;
    }

}
