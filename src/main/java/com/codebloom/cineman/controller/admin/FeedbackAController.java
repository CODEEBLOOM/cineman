package com.codebloom.cineman.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.FeedbackService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}/admin/feedbacks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin Feedback Controller", description = "Quản lý feedback cho admin")
public class FeedbackAController {

    private final FeedbackService feedbackService;
    
    // admin lấy toàn bộ feedback có isActive = true
    @GetMapping
    public ResponseEntity<ApiResponse> getAllActiveFeedbacks() {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy tất cả phản hồi (isActive=true) thành công.")
                .data(feedbackService.findAll())
                .build()
        );
    }
    
    // admin lấy toàn bộ feedback bao gồm cả isActive = false ( check lịch sử và thống kê )
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllFeedbacksIncludeInactive() {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy tất cả phản hồi (bao gồm đã xoá mềm) thành công.")
                .data(feedbackService.findAllIncludeInactive())
                .build()
        );
    }
    
    // admin lấy feedback theo id của feedback cần tìm ( isActive = true )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFeedbackById(@PathVariable Integer id) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy phản hồi thành công.")
                .data(feedbackService.findById(id))
                .build()
        );
    }

    // admin lấy feedback theo email của user cần tìm ( isActive = true )	
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse> getFeedbacksByUserEmail(@RequestParam String email) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy phản hồi theo email thành công.")
                .data(feedbackService.findByUser(email))
                .build()
        );
    }
    
 // admin lấy feedback theo mức độ đánh giá cần tìm ( isActive = true )
    @GetMapping("/satisfaction")
    public ResponseEntity<ApiResponse> getFeedbacksBySatisfaction(@RequestParam SatisfactionLevel level) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lọc phản hồi theo mức độ hài lòng thành công.")
                .data(feedbackService.findBySatisfactionLevel(level))
                .build()
        );
    }
    // admin có thể xóa feedback của user nếu đó là feedback tiêu cực ( xóa mềm )
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteFeedbackAsAdmin(@PathVariable Integer id) {
        feedbackService.deleteByAdmin(id); 
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Admin đã xoá phản hồi thành công.")
                .data(id)
                .build()
        );
    }
}
