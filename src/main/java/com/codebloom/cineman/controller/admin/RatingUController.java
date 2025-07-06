package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.RatingRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.model.UserPrincipal; 
import com.codebloom.cineman.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/user/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Rating Controller", description = "Quản lý đánh giá phim của người dùng")
public class RatingUController {

    private final RatingService ratingService;

    // Thêm đánh giá phim mới
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> rateMovie(@Valid @RequestBody RatingRequest request,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId(); 
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đánh giá phim thành công.")
                .data(ratingService.saveRating(userId, request))
                .build()
        );
    }

    // Xem đánh giá theo mã vé
    @GetMapping("/my-rating/ticket/{ticketId}")
    public ResponseEntity<ApiResponse> getMyTicketRating(@PathVariable Long ticketId,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá thành công.")
                .data(ratingService.getRatingByTicket(userId, ticketId))
                .build()
        );
    }

    // Lấy toàn bộ đánh giá cá nhân
    @GetMapping("/all-ratings")
    public ResponseEntity<ApiResponse> getMyRatings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy toàn bộ đánh giá cá nhân thành công.")
                .data(ratingService.getRatingsByUser(userId))
                .build()
        );
    }

    // Lấy đánh giá theo phim
    @GetMapping("/my-rating/movie/{movieId}")
    public ResponseEntity<ApiResponse> getMyRatingsByMovie(@PathVariable Integer movieId,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá cá nhân theo phim thành công.")
                .data(ratingService.getRatingsByUserAndMovie(userId, movieId))
                .build()
        );
    }
}
