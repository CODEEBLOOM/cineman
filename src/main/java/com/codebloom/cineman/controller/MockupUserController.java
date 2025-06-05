package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.ChangePasswordRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mockup/user")
@Tag(name = "Mockup User Controller ( User api )")
public class MockupUserController {

    @Operation(summary = "Get all users", description = "API dùng để lấy ra toàn bộ users có trong hệ thống.")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setUserId(1L);
        userResponse1.setEmail("admin@codebloom.com");
        userResponse1.setPassword("admin");
        userResponse1.setFullName("Admin");
        userResponse1.setPhoneNumber("0446354437");
        userResponse1.setAddress("District 12 - Ho Chi Minh");
        UserResponse userResponse2 = new UserResponse();
        userResponse2.setUserId(2L);
        userResponse2.setEmail("admin01@codebloom.com");
        userResponse2.setPassword("admin01");
        userResponse2.setFullName("Admin01");
        userResponse2.setPhoneNumber("0446354473");
        userResponse2.setAddress("District 12 - Ho Chi Minh");

        List<UserResponse> userList = List.of(userResponse1, userResponse2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", userList);
        result.put("message", "User List");
        result.put("status", HttpStatus.OK.value());

        return result;
    }

    @Operation(summary = "Find User By User Id", description = "API dùng để lấy ra user theo user_id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable Long userId) {
        UserResponse userDetail = new UserResponse();
        userDetail.setUserId(1L);
        userDetail.setEmail("admin@codebloom.com");
        userDetail.setPassword("admin");
        userDetail.setFullName("Admin");
        userDetail.setPhoneNumber("0446354437");
        userDetail.setAddress("District 12 - Ho Chi Minh");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", userDetail);
        result.put("message", "User List");
        result.put("status", HttpStatus.OK.value());
        return result;
    }

    @Operation(summary = "Create New User", description = "API dùng để tạo mới một User")
    @PostMapping("/add")
    public Map<String, Object> createUser(UserCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User Created Success");
        result.put("data", 3);
        return result;
    }

    @Operation(summary = "Update User", description = "API dùng để cập nhật một User")
    @PutMapping("/update")
    public Map<String, Object> updateUser(UserUpdateRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User Updated Success");
        result.put("data", "");
        return result;
    }

    @Operation(summary = "Change Password", description = "API dùng để thay đổi mật khẩu của User")
    @PutMapping("/change-pwd")
    public Map<String, Object> changePassword(ChangePasswordRequest request) {
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
