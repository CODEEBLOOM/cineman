package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.MovieReviewRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/movie/{movieId}/reviews")
@RequiredArgsConstructor
@Validated
@Tag(name = "Movie Review Controller")
public class MovieReviewController {

    private final MovieReviewService movieReviewService;

    @Operation(summary = "Get movie reviews", description = "Api dung de lay danh sach review cong khai cua phim")
    @GetMapping
    public ResponseEntity<ApiResponse> getMovieReviews(
            @PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") Integer movieId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be greater than or equal to 1")
            @Max(value = 100, message = "Size must be less than or equal to 100") Integer size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieReviewService.findAllByMovieId(movieId, page, size))
                        .build()
        );
    }

    @Operation(summary = "Get current user movie review context", description = "Api dung de kiem tra user hien tai co du dieu kien review phim hay khong")
    @GetMapping("/eligibility")
    public ResponseEntity<ApiResponse> getMovieReviewEligibility(
            @PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") Integer movieId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieReviewService.getReviewContext(movieId))
                        .build()
        );
    }

    @Operation(summary = "Create movie review", description = "Api dung de tao review cho phim sau khi da mua ve va thanh toan thanh cong")
    @PostMapping
    public ResponseEntity<ApiResponse> createMovieReview(
            @PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") Integer movieId,
            @RequestBody @Valid MovieReviewRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieReviewService.create(movieId, request))
                        .build()
        );
    }

    @Operation(summary = "Update current user movie review", description = "Api dung de cap nhat review cua chinh user hien tai")
    @PutMapping
    public ResponseEntity<ApiResponse> updateMovieReview(
            @PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") Integer movieId,
            @RequestBody @Valid MovieReviewRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieReviewService.update(movieId, request))
                        .build()
        );
    }

    @Operation(summary = "Delete current user movie review", description = "Api dung de xoa review cua chinh user hien tai")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteMovieReview(
            @PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") Integer movieId
    ) {
        movieReviewService.delete(movieId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(null)
                        .build()
        );
    }
}
