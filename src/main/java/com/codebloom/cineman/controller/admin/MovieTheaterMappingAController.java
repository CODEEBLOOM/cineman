package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieTheaterMappingRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieTheaterMappingService;
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
@Validated
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/movie-theater-mapping")
@Tag(name = "Movie Theater Mapping Controller", description = "Quan ly rap co the chieu cua phim.")
public class MovieTheaterMappingAController {

    private final MovieTheaterMappingService movieTheaterMappingService;

    @Operation(summary = "Add movie theater mapping", description = "API dung de gan phim voi rap chieu.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid MovieTheaterMappingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Add movie theater mapping success")
                        .data(movieTheaterMappingService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Update movie theater mapping", description = "API dung de cap nhat gan phim voi rap chieu.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> update(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid MovieTheaterMappingRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update movie theater mapping success")
                        .data(movieTheaterMappingService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete movie theater mapping", description = "API dung de xoa gan phim voi rap chieu.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> delete(@PathVariable @Min(1) Integer id) {
        movieTheaterMappingService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete movie theater mapping success")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Find all movie theater mappings", description = "API dung de lay tat ca gan phim voi rap chieu.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAll(PageRequest pageRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieTheaterMappingService.findAllByPage(pageRequest))
                        .build()
        );
    }

    @Operation(summary = "Find movie theater mapping by id", description = "API dung de lay chi tiet gan phim voi rap chieu.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieTheaterMappingService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Find mappings by movie id", description = "API dung de lay danh sach rap cua mot phim.")
    @GetMapping("/movie/{movieId}/all")
    public ResponseEntity<ApiResponse> findAllByMovieId(@PathVariable @Min(1) Integer movieId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieTheaterMappingService.findAllByMovieId(movieId))
                        .build()
        );
    }

    @Operation(summary = "Find mappings by movie theater id", description = "API dung de lay danh sach phim cua mot rap.")
    @GetMapping("/movie-theater/{movieTheaterId}/all")
    public ResponseEntity<ApiResponse> findAllByMovieTheaterId(@PathVariable @Min(1) Integer movieTheaterId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieTheaterMappingService.findAllByMovieTheaterId(movieTheaterId))
                        .build()
        );
    }
}
