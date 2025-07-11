package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.ShowTimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/show-times")
@Tag(name = "ShowTime", description = "API showtimes for customers")
@Validated
public class ShowTimeController {

    private final ShowTimeService showTimeService;

    @GetMapping("/movie/{movieId}/movie-theater/{movieTheaterId}")
    @Operation(summary = "Find show time by movie id and movie theater id", description = "API dùng để lấy  show time theo movie id và movie theater id ")
    public ResponseEntity<ApiResponse> finAllShowTimeByMovieIdAndMovieTheaterId(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's movie theater is must be greater than 0") Integer movieTheaterId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findAllShowTimeByMovieIdAndMovieTheaterId(movieId, movieTheaterId))
                        .build()
        );
    }

    @GetMapping("/movie/{movieId}/movie-theater/{movieTheaterId}/by-date")
    @Operation(summary = "Find show time by show date", description = "API dùng để lấy  show time theo show date ")
    public ResponseEntity<ApiResponse> finAllShowTimeByMovieIdAndMovieTheaterIdAndShowDate(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's movie theater is must be greater than 0") Integer movieTheaterId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")  Date showDate
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findAllShowTimeByMovieIdAndMovieTheaterIdAndShowDateEqual(movieId, movieTheaterId, showDate))
                        .build()
        );
    }


    @GetMapping("/{id}")
    @Operation(summary = "Find show time by show time id", description = "API dùng để lấy  show time theo show time id ")
    public ResponseEntity<ApiResponse> findAllByShowTimeId(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findCountByShowTimeId(id))
                        .build()
        );
    }

}
