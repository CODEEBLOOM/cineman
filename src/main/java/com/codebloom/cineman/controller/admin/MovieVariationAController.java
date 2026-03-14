package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieVariationRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieVariationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/movie-variation")
@Tag(name = "Movie Variation Controller", description = "Quan ly dinh dang suat chieu")
@Validated
public class MovieVariationAController {

    private final MovieVariationService movieVariationService;

    @Operation(summary = "Get all movie variations")
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

    @Operation(summary = "Get movie variation by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieVariationById(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieVariationService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create movie variation")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createMovieVariation(@RequestBody @Valid MovieVariationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create movie variation successfully")
                        .data(movieVariationService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Update movie variation")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateMovieVariation(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid MovieVariationRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update movie variation successfully")
                        .data(movieVariationService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete movie variation")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteMovieVariation(@PathVariable @Min(1) Integer id) {
        movieVariationService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete movie variation successfully")
                        .data(null)
                        .build()
        );
    }
}
