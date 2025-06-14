package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieGenreRequest;
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

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "Movie Genre Controller")
@RequestMapping("/admin/movie-genre")
@Validated
@RequiredArgsConstructor
public class MovieGenreController {

    private final MovieGenreService movieGenreService;
    private Map<String, Object> response;

    @Operation(summary = "Add genre for movie", description = "API dùng để gán thể loại phim cho bộ phim.")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addGenreForMovie(@RequestBody @Valid MovieGenreRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Add genre for movie success");
        response.put("data", movieGenreService.addMovieGenre(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Add genre for movie", description = "API dùng để gán thể loại phim cho bộ phim.")
    @PutMapping("/genre/{id}/update")
    public ResponseEntity<Map<String, Object>> updateGenreForMovie(
            @PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id,
            @RequestBody @Valid MovieGenreRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Update genre for movie success");
        response.put("data", movieGenreService.updateGenreOfMovie(request, id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Delete genre of movie", description = "API dùng để xóa thể loại phim của bộ phim.")
    @DeleteMapping("/{movieId}/{genreId}/delete")
    public ResponseEntity<Map<String, Object>> removeGenreOfMovie(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer genreId
    ) {

        MovieGenreRequest request = new MovieGenreRequest();
        request.setMovieId(movieId);
        request.setGenreId(genreId);

        movieGenreService.deleteMovieGenre(request);
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Update genre for movie success");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



}
