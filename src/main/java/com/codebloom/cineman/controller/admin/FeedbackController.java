package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    // yêu cầu login
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        FeedbackResponse created = feedbackService.create(request, userEmail);
        return ResponseEntity.ok(created);
    }
    
    
 // test không cần login
//    @PostMapping
//    public ResponseEntity<FeedbackResponse> createFeedback(
//            @Valid @RequestBody FeedbackRequest request,
//            @RequestParam String email) { //test nhanh
//
//        FeedbackResponse created = feedbackService.create(request, email);
//        return ResponseEntity.ok(created);
//    }

    // yêu cầu login
    @GetMapping("/myfeedback")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedbacks(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        List<FeedbackResponse> feedbacks = feedbackService.findByUser(userEmail);
        return ResponseEntity.ok(feedbacks);
    }
    
    // test không cần login
//    @GetMapping("/myfeedback")
//    public ResponseEntity<List<FeedbackResponse>> getMyFeedbacks(@RequestParam String email) {
//        List<FeedbackResponse> feedbacks = feedbackService.findByUser(email);
//        return ResponseEntity.ok(feedbacks);
//    }

    // cần login
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Integer id,
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        FeedbackResponse updated = feedbackService.update(id, request, userEmail);
        return ResponseEntity.ok(updated);
    }
    
    // test không cần login 
//    @PutMapping("/{id}")
//    public ResponseEntity<FeedbackResponse> updateFeedback(
//            @PathVariable Integer id,
//            @Valid @RequestBody FeedbackRequest request,
//            @RequestParam String email) {
//
//        FeedbackResponse updated = feedbackService.update(id, request, email);
//        return ResponseEntity.ok(updated);
//    }

    // cần login
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        feedbackService.delete(id, userEmail);
        return ResponseEntity.noContent().build();
    }
    
    // test
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFeedback(
//            @PathVariable Integer id,
//            @RequestParam String email) {
//
//        feedbackService.delete(id, email);
//        return ResponseEntity.noContent().build();
//    }


    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Integer id) {
        return ResponseEntity.ok(feedbackService.findById(id));
    }
    
    @GetMapping("/satisfaction")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksBySatisfactionLevel(
        @RequestParam SatisfactionLevel level) {
        List<FeedbackResponse> feedbacks = feedbackService.findBySatisfactionLevel(level);
        return ResponseEntity.ok(feedbacks);
    }

}
