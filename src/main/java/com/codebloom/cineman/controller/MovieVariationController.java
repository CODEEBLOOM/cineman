package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieVariationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/movie-variation")
@Tag(name = "Movie Variation Client Controller")
public class MovieVariationController {

    private final MovieVariationService movieVariationService;

    @Operation(summary = "Get all active movie variations")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovieVariations() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieVariationService.findAll())
                        .build()
        );
    }
}
