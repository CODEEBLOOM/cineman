package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/movie")
@RequiredArgsConstructor
@Tag(name = "Movie Client Controller")
@Validated
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Get all movie", description = "Api dùng để client lấy tất cả movie trong hệ thống không xác thực")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovie( MoviePageQueryRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieService.findAllByPage(req))
                        .build()
        );
    }

    @Operation(summary = "Get all movie by movie theater", description = "Api dùng để client lấy tất cả movie trong hệ thống theo id rạp chiếu")
    @GetMapping("/movie-theater/{id}")
    public ResponseEntity<ApiResponse> getAllMovieByMovieTheater(
            @PathVariable @Min(value = 1, message = "Id's movie theater is must be greater than or equal 1") Integer id,
            MoviePageQueryRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieService.findAllByPageAndFilter(req, id))
                        .build()
        );
    }

    @Operation(summary = "Get movie by id", description = "Api dùng để client lấy chi tiết của một bộ phim bằng id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById( @PathVariable @Min(value = 1, message = "Id's movie is must be greater than or equal 1") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(movieService.findById(id))
                        .build()
        );
    }

}
