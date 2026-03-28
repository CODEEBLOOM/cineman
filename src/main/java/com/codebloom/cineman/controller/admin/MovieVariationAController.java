package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieVariationRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieVariationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/movie-variation")
@Tag(name = "Movie Variation Controller")
@Validated
public class MovieVariationAController {

    private final MovieVariationService movieVariationService;

    @Operation(summary = "Get all movie variation", description = "API dung de lay ra toan bo movie variation dang hoat dong.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovieVariation() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieVariationService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Get movie variation by id", description = "API dung de lay ra movie variation theo id.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieVariationById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieVariationService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create movie variation", description = "API dung de tao moi movie variation.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createMovieVariation(@RequestBody @Valid MovieVariationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(movieVariationService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Update movie variation", description = "API dung de cap nhat thong tin movie variation.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateMovieVariation(
            @PathVariable Integer id,
            @RequestBody @Valid MovieVariationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(movieVariationService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete movie variation", description = "API dung de xoa mem movie variation.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteMovieVariation(@PathVariable Integer id) {
        movieVariationService.delete(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }
}
