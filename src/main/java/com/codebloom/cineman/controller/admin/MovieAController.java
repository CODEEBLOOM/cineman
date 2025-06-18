package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieService;
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
@RequestMapping("${api.path}/admin/movie")
@Validated
@RequiredArgsConstructor
@Tag(name = "Movie Controller")
public class MovieAController {

    private final MovieService movieService;

    @Operation(summary = "Get all movies", description = "API dùng để lấy ra toàn bộ phim có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovies(MoviePageQueryRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieService.findAllByPage(request))
                        .build()
        );
    }


    @Operation(summary = "Find Movie By Movie_id", description = "API dùng để lấy ra chi tiết của một bộ phim theo movie_id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById(@PathVariable @Min(value = 1, message = "Id's movie is must be greater than or equal 1") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create New Movie", description = "API dùng để tạo mới một Movie")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createMovie(@RequestBody @Valid MovieCreationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Created movie successfully")
                        .data(movieService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Update movie by ID", description = "API dùng để  update một Movie")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateMovie(@RequestBody @Valid MovieUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Updated movie successfully")
                        .data(movieService.update(request))
                        .build()
        );
    }
    @Operation(summary = "Delete movie by ID", description = "API dùng để xóa  một Movie")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteMovie(@PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer id) {
        movieService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Delete Movie Successfully")
                        .data(null)
                        .build()
        );
    }



}

