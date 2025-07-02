
package com.codebloom.cineman.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.RatingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}/admin/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin Rating Controller", description = "Quản lý đánh giá phim của người dùng dành cho admin")
public class RatingAController {

    private final RatingService ratingService;

    /**
     * Admin hoặc public lấy toàn bộ đánh giá theo ID phim
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse> getRatingsByMovie(@PathVariable Integer movieId) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá theo phim thành công.")
                .data(ratingService.getRatingsByMovie(movieId))
                .build()
        );
    }

    /**
     * Admin lấy đánh giá của người dùng theo email (chỉ nếu user đang ACTIVE)
     */
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse> getRatingsByUserEmail(@RequestParam String email) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá theo email thành công.")
                .data(ratingService.getRatingsByEmail(email))
                .build()
        );
    }
}
