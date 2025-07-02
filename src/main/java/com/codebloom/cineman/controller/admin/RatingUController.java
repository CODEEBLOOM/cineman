
package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.RatingRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/user/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Rating Controller", description = "Quản lý đánh giá phim của người dùng")
public class RatingUController {

    private final RatingService ratingService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> rateMovie(@Valid @RequestBody RatingRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đánh giá phim thành công.")
                .data(ratingService.saveRating(userId, request))
                .build()
        );
    }

    @GetMapping("/my-rating/ticket/{ticketId}")
    public ResponseEntity<ApiResponse> getMyTicketRating(@PathVariable Long ticketId,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá thành công.")
                .data(ratingService.getRatingByTicket(userId, ticketId))
                .build()
        );
    }

 
    @GetMapping("/all-ratings")
    public ResponseEntity<ApiResponse> getMyRatings(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy toàn bộ đánh giá cá nhân thành công.")
                .data(ratingService.getRatingsByUser(userId))
                .build()
        );
    }
    
    
    @GetMapping("/my-rating/movie/{movieId}")
    public ResponseEntity<ApiResponse> getMyRatingsByMovie(@PathVariable Integer movieId,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá cá nhân theo phim thành công.")
                .data(ratingService.getRatingsByUserAndMovie(userId, movieId))
                .build()
        );
    }

}
