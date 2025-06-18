package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieGenreRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieGenreService;
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
@Tag(name = "Movie Genre Controller")
@RequestMapping("/admin/movie-genre")
@Validated
@RequiredArgsConstructor
public class MovieGenreAController {

    private final MovieGenreService movieGenreService;

    @Operation(summary = "Add genre for movie", description = "API dùng để gán thể loại phim cho bộ phim.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addGenreForMovie(@RequestBody @Valid MovieGenreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Add genre for movie success")
                        .data(movieGenreService.addMovieGenre(request))
                        .build()
        );
    }


    @Operation(summary = "Add genre for movie", description = "API dùng để gán thể loại phim cho bộ phim.")
    @PutMapping("/genre/{id}/update")
    public ResponseEntity<ApiResponse> updateGenreForMovie(
            @PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id,
            @RequestBody @Valid MovieGenreRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update genre for movie success")
                        .data(movieGenreService.updateGenreOfMovie(request, id))
                        .build()
        );
    }

    @Operation(summary = "Delete genre of movie", description = "API dùng để xóa thể loại phim của bộ phim.")
    @DeleteMapping("/{movieId}/{genreId}/delete")
    public ResponseEntity<ApiResponse> removeGenreOfMovie(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer genreId
    ) {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(movieId);
        request.setGenreId(genreId);
        movieGenreService.deleteMovieGenre(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete genre for movie success")
                        .data(null)
                        .build()
        );
    }



}
