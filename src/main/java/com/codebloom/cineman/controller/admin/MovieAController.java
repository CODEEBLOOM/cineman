package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;
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

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("/admin/movie")
@Validated
@RequiredArgsConstructor
@Tag(name = "Movie Controller")
public class MovieAController {

    private final MovieService movieService;
    private Map<String, Object> response;

    @Operation(summary = "Get all movies", description = "API dùng để lấy ra toàn bộ phim có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMovies(PageQueryRequest request) {
        MoviePageableResponse pageMovie = movieService.findAllByPage(request);
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Success");
        response.put("data", pageMovie);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "Find Movie By Movie_id", description = "API dùng để lấy ra chi tiết của một bộ phim theo movie_id")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMovieById(@PathVariable @Min(value = 1, message = "Id's movie is must be greater than or equal 1") Integer id) {
        MovieResponse movie = movieService.findById(id);
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Movie Created Successfully");
        response.put("data", movie);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Create New Movie", description = "API dùng để tạo mới một Movie")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createMovie(@RequestBody @Valid MovieCreationRequest request) {
        MovieEntity movieId = movieService.save(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Movie Created Successfully");
        result.put("data", movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Delete movie by ID", description = "API dùng để xóa  một Movie")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteMovie(@PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer id) {
        movieService.delete(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Delete Movie Successfully");
        response.put("data", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }


    @Operation(summary = "Update movie by ID", description = "API dùng để  update một Movie")
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateMovie(
            @RequestBody @Valid MovieUpdateRequest request) {
        MovieResponse updated = movieService.update( request);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Cập nhật thành công");
        response.put("data", updated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

