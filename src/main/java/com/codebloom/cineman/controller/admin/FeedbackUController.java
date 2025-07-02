package com.codebloom.cineman.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
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
    
    // thêm feedback mới
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createFeedback(@Valid @RequestBody FeedbackRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo phản hồi thành công.")
                .data(feedbackService.save(request, userEmail))
                .build()
        );
    }
    
    // user chỉ có thể xem feedback của bản thân
    @GetMapping("/my-feedbacks")
    public ResponseEntity<ApiResponse> getMyFeedbacks(@AuthenticationPrincipal UserDetails userDetails) {
      String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy phản hồi cá nhân thành công.")
                .data(feedbackService.findByUser(userEmail))
                .build()
        );
    }
    
}
