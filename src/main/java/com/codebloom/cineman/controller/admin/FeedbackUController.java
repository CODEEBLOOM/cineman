package com.codebloom.cineman.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.service.FeedbackService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}/user/feedbacks")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Feedback Controller", description = "Quản lý feedback của người dùng")
public class FeedbackUController {

    private final FeedbackService feedbackService;

    // Tạo phản hồi mới
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createFeedback(@Valid @RequestBody FeedbackRequest request,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo phản hồi thành công.")
                .data(feedbackService.save(request, userId)) 
                .build()
        );
    }

    // Người dùng chỉ xem feedback của chính họ
    @GetMapping("/my-feedbacks")
    public ResponseEntity<ApiResponse> getMyFeedbacks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId(); 
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy phản hồi cá nhân thành công.")
                .data(feedbackService.findByUser(userId)) 
                .build()
        );
    }
}
