package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/feedbacks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Feedback Controller", description = "Quản lý feedback của người dùng")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "Tạo feedback mới")
    @PostMapping
    public ResponseEntity<ApiResponse> createFeedback(
            @Valid @RequestBody FeedbackRequest request,
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
    

    @Operation(summary = "Lấy danh sách feedback của người dùng đang login")
    @GetMapping("/my-feedbacks")
    public ResponseEntity<ApiResponse> getMyFeedbacks(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách phản hồi cá nhân thành công.")
                        .data(feedbackService.findByUser(userEmail))
                        .build()
        );
    }


    @Operation(summary = "Cập nhật feedback")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFeedback(
            @PathVariable @Min(value = 1, message = "ID phải >= 1") Integer id,
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật phản hồi thành công.")
                        .data(feedbackService.update(id, request, userEmail))
                        .build()
        );
    }
    
   

    @Operation(summary = "Xóa feedback")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFeedback(
            @PathVariable @Min(value = 1, message = "ID phải >= 1") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        feedbackService.delete(id, userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Xóa phản hồi thành công.")
                        .data(id)
                        .build()
        );
    }
    

    @Operation(summary = "Lấy tất cả feedback (Admin)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllFeedbacks() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy tất cả phản hồi thành công.")
                        .data(feedbackService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Lấy một feedback theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFeedbackById(
            @PathVariable @Min(value = 1, message = "ID phải >= 1") Integer id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy phản hồi thành công.")
                        .data(feedbackService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Lọc feedback theo mức độ hài lòng")
    @GetMapping("/satisfaction")
    public ResponseEntity<ApiResponse> getFeedbacksBySatisfactionLevel(
            @RequestParam SatisfactionLevel level) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Lọc phản hồi theo mức độ hài lòng thành công.")
                        .data(feedbackService.findBySatisfactionLevel(level))
                        .build()
        );
    }
}
