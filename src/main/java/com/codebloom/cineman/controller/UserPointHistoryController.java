package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.UserPointHistoryService;
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
@RequestMapping("${api.path}/user-point-history")
@Tag(name = "User Point History")
@Validated
@RequiredArgsConstructor
public class UserPointHistoryController {

    private final UserPointHistoryService userPointHistoryService;

    @Operation(summary = "Create User Point History", description = "API dùng để tạo mới một lịch sử đổi điểm cho người dùng")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createTransaction(@RequestBody @Valid UserPointHistoryRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Create transaction successfully")
                                .data(userPointHistoryService.createTransaction(request))
                                .build()
                );
    }

    @Operation(summary = "Update User Point History", description = "API dùng cập nhật mới một lịch sử đổi điểm cho người dùng")
    @PostMapping("/refund/{vnTnxRef}")
    public ResponseEntity<ApiResponse> refundTransaction(
            @RequestBody @Valid UserPointHistoryRequest request,
            @PathVariable @NotBlank(message = "vnTnxRef không được phép trống !") String vnTnxRef) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Create transaction successfully")
                                .data(userPointHistoryService.refundTransaction(request, vnTnxRef))
                                .build()
                );
    }


}