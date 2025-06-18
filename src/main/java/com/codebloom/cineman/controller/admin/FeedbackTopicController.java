package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;
import com.codebloom.cineman.service.FeedbackTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback-topic")
@RequiredArgsConstructor
public class FeedbackTopicController {

    private final FeedbackTopicService feedbackTopicService;

    // Lấy tất cả chủ đề
    @GetMapping
    public ResponseEntity<List<FeedbackTopicResponse>> getAllTopics() {
        return ResponseEntity.ok(feedbackTopicService.findAll());
    }

    // Lấy 1 chủ đề theo ID
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackTopicResponse> getTopicById(@PathVariable Integer id) {
        return ResponseEntity.ok(feedbackTopicService.findById(id));
    }

    // Tạo mới chủ đề
    @PostMapping
    public ResponseEntity<FeedbackTopicResponse> createTopic(@RequestBody FeedbackTopicRequest request) {
        return ResponseEntity.ok(feedbackTopicService.create(request));
    }

    // Cập nhật chủ đề theo ID
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackTopicResponse> updateTopic(@PathVariable Integer id,
                                                             @RequestBody FeedbackTopicRequest request) {
        return ResponseEntity.ok(feedbackTopicService.update(id, request));
    }

    // Xóa chủ đề theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Integer id) {
        feedbackTopicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
