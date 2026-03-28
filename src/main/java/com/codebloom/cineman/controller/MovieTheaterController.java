package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieTheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}/movie-theater")
@RequiredArgsConstructor
@Tag(name = "Movie Theater Client Controller")
public class MovieTheaterController {

    private final MovieTheaterService movieTheaterService;

    @Operation(summary = "Get all movie theaters", description = "API dùng để client lấy tất cả thông tin rạp chiếu phim không phân trang")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovieTheaters() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieTheaterService.findAll())
                        .build()
        );
    }
}
