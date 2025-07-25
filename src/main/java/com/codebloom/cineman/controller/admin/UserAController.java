package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.path}/admin/user")
@Tag(name = "User Controller ( User api )")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
public class UserAController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "API dùng để lấy ra toàn bộ users có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers(PageRequest pageRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(userService.findAll(pageRequest))
                        .build()
        );
    }

    @Operation(summary = "Find User By User Id", description = "API dùng để lấy ra user theo user_id")
    @GetMapping("/detail/{userId}")
    public ResponseEntity<ApiResponse> getUserDetail(@PathVariable @Min(1) Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(userService.findById(userId))
                        .build()
        );
    }

    @Operation(summary = "Create New User", description = "API dùng để tạo mới một user bên admin")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("User Created Success")
                        .data(userService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Update User", description = "API dùng để cập nhật một User")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request) {
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("User Updated Success")
                        .data(userService.update(request))
                        .build()
        );
    }


    @Operation(summary = "Disable User", description = "API dùng để chuyển isActive thành false")
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Confirm User", description = "API dùng để confirm email")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm Email {}", secretCode);
        try {
            userService.confirmEmail(secretCode);
            response.sendRedirect("http://localhost:3000");
        }catch(Exception e) {
            log.error("Error occur confirm email, error: {}", e.getMessage());
        }finally {
            response.sendRedirect("https://www.facebook.com");
        }
    }

}
