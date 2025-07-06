package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.FeedbackTopicService;
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
@RequestMapping("${api.path}/admin/feedback-topics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Feedback Topic Controller")
public class FeedbackTopicController {

    private final FeedbackTopicService feedbackTopicService;

    @Operation(summary = "Lấy tất cả chủ đề phản hồi")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTopics() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy danh sách chủ đề phản hồi thành công.")
                        .status(HttpStatus.OK.value())
                        .data(feedbackTopicService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Lấy một chủ đề phản hồi theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTopicById(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy thông tin chủ đề phản hồi thành công.")
                        .status(HttpStatus.OK.value())
                        .data(feedbackTopicService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Tạo mới một chủ đề phản hồi")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createTopic(@Valid @RequestBody FeedbackTopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Tạo chủ đề phản hồi thành công.")
                        .status(HttpStatus.CREATED.value())
                        .data(feedbackTopicService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật một chủ đề phản hồi")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateTopic(@PathVariable @Min(1) Integer id,
                                                   @Valid @RequestBody FeedbackTopicRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Cập nhật chủ đề phản hồi thành công.")
                        .status(HttpStatus.OK.value())
                        .data(feedbackTopicService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Xóa một chủ đề phản hồi")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteTopic(@PathVariable @Min(1) Integer id) {
        feedbackTopicService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Xóa chủ đề phản hồi thành công.")
                        .status(HttpStatus.OK.value())
                        .data(id)
                        .build()
        );
    }
}
