package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/movie")
@Validated
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Get all movies", description = "API dùng để lấy ra toàn bộ phim có trong hệ thống.")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllMovies(PageQueryRequest request) {
        MoviePageableResponse response = movieService.findAllByPage(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Paged movie list from database");
        result.put("data", response);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @Operation(summary = "Find Movie By Movie_id", description = "API dùng để lấy ra chi tiết của một bộ phim theo movie_id")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMovieById(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") /* @Min là dùng để xác định giá trị phải lớn hơn hoặc bằng giá trị được chỉ định*/ Integer id) {
        MovieResponse movie = movieService.findById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Movie Created Successfully");
        result.put("data", movie);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "Create New Movie", description = "API dùng để tạo mới một Movie")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createMovie(@RequestBody @Valid MovieCreationRequest request) {
        Integer movieId = movieService.save(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Movie Created Successfully");
        result.put("data", movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Delete movie by ID", description = "API dùng để xóa  một Movie")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteMovie(@PathVariable Integer id) {
        movieService.delete(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Đã chuyển trạng thái phim sang Hủy chiếu ");
        response.put("data", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }


    @Operation(summary = "Update movie by ID", description = "API dùng để  update một Movie")
    @PutMapping("/{id}/update")
    public ResponseEntity<Map<String, Object>> updateMovie(
            @PathVariable Integer id,
            @RequestBody MovieUpdateRequest request) {
        MovieResponse updated = movieService.update(id, request);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Cập nhật thành công");
        response.put("data", updated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Find movies by title", description = "API dùng để tìm phim theo tên")
    @GetMapping("/search")
    //  http://localhost:8081/movie/search?title=Anh hùng trái đất
    public ResponseEntity<List<MovieResponse>> searchMoviesByTitle(@RequestParam String title) {
        List<MovieResponse> movies = movieService.findByTitle(title);
        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }
}

