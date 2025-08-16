package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.request.UserRegisterRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.UserService;
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
@RequiredArgsConstructor
@RequestMapping("${api.path}/user")
@Validated
@Tag(name = " Controller User - Customer", description = "API quản lý người dùng")
public class UserController {

    private final UserService userService;

    @Operation(summary = "API đổi điểm tích lũy", description = "API đổi điểm tích lũy")
    @PostMapping("/{userId}/change-point/{savePoint}")
    public ResponseEntity<ApiResponse> getMoneyFromSavePoint(
        @PathVariable("userId") @Min( value = 1, message = "Id của người dùng phải lớn hơn 0 !") Long userId,
        @PathVariable("savePoint") @Min( value = 0, message = "Điểm  đổi phải lớn hơn 0 !")  Integer savePoint
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Tạo tài khoản người dùng thành công")
                        .status(HttpStatus.OK.value())
                        .data(userService.getMoneyFromSavePointOfUser(savePoint, userId))
                        .build()
        );
    }

    @Operation(summary = "API cập nhật thông tin người dùng", description = "API cập nhật thông tin người dùng không đổi mật khẩu")
    @PutMapping("/{userId}/update-info")
    public ResponseEntity<ApiResponse> updateInfoUser(
            @RequestBody @Valid UserUpdateRequest request,
            @PathVariable("userId") @Min( value = 1, message = "Id của người dùng phải lớn hơn 0 !") Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Tạo tài khoản người dùng thành công")
                        .status(HttpStatus.OK.value())
                        .data(userService.updateInfoUser(userId,request))
                        .build()
        );
    }

}
