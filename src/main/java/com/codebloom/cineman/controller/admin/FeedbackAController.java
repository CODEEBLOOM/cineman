package com.codebloom.cineman.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.FeedbackService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}/admin/feedbacks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin Feedback Controller", description = "Quản lý feedback cho admin")
public class FeedbackAController {

    private final FeedbackService feedbackService;

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFeedbackById(@PathVariable("id") @Min(1) Integer id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy phản hồi thành công.")
                        .data(feedbackService.findById(id))
                        .build()
        );
    }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse> getFeedbacksByUserEmail(@RequestParam String email) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy phản hồi theo email thành công.")
                        .data(feedbackService.findByUserEmail(email))
                        .build()
        );
    }

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

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteFeedbackAsAdmin(@PathVariable("id") @Min(1) Integer id) {
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
