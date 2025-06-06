package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieStatusService movieStatusService;

    @Operation(summary = "Get all movies", description = "API dùng để lấy ra toàn bộ phim có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMovies() {
        List<MovieResponse> movies = movieService.findAll();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Movie list from database");
        result.put("data", movies);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Find Movie By Movie_id", description = "API dùng để lấy ra phim theo movie_id")
    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Integer id) {
        MovieResponse movie = movieService.findById(id);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Create New Movie", description = "API dùng để tạo mới một Movie")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createMovie(@RequestBody MovieCreationRequest request) {
        Integer movieId = movieService.save(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Movie Created Successfully");
        result.put("data", movieId);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Delete movie by ID",description = "API dùng để xóa  một Movie")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Integer id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update movie by ID" ,description = "API dùng để  update một Movie")
    @PutMapping("/update/{id}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Integer id,
            @RequestBody MovieUpdateRequest request) {
        MovieResponse updated = movieService.update(id, request);
        return ResponseEntity.ok(updated);
    }
}
