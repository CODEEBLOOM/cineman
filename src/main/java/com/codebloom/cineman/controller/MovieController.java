package com.codebloom.cineman.controller;

import com.codebloom.cineman.Exception.ErrorResponse;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
@Validated
public class MovieController {

    private final MovieService movieService;
    private final MovieStatusService movieStatusService;

    @Operation(summary = "Get all movies", description = "API dùng để lấy ra toàn bộ phim có trong hệ thống.")
    @GetMapping("/all") //http://localhost:8081/movie/all?page=0&size=20
    public ResponseEntity<Map<String, Object>> getAllMovies(PageQueryRequest request) {
        Page<MovieResponse> page = movieService.findAllByPage(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Paged movie list from database");
        result.put("data", page.getContent());
        result.put("currentPage", page.getNumber());
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Find Movie By Movie_id", description = "API dùng để lấy ra phim theo movie_id")

    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") /* @Min là dùng để xác định giá trị phải lớn hơn hoặc bằng giá trị được chỉ định*/ Integer id) {
        MovieResponse movie = movieService.findById(id);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Create New Movie", description = "API dùng để tạo mới một Movie")
  //  Giúp tài liệu Swagger hiển thị rõ ràng các trường hợp trả về
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success"),
//            @ApiResponse(responseCode = "404", description = "Not Found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
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
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success"),
//            @ApiResponse(responseCode = "404", description = "Not Found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteMovie(@PathVariable Integer id) {
        movieService.delete(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Đã chuyển trạng thái phim sang Hủy chiếu ");
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update movie by ID", description = "API dùng để  update một Movie")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success"),
//            @ApiResponse(responseCode = "404", description = "Not Found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateMovie(
            @PathVariable Integer id,
            @RequestBody MovieUpdateRequest request) {

        MovieResponse updated = movieService.update(id, request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Cập nhật thành công");
        response.put("data", updated);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Find movies by title", description = "API dùng để tìm phim theo tên")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success"),
//            @ApiResponse(responseCode = "404", description = "Not Found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
    @GetMapping("/search")
    //  http://localhost:8081/movie/search?title=Anh hùng trái đất
    public ResponseEntity<List<MovieResponse>> searchMoviesByTitle(@RequestParam String title) {
        List<MovieResponse> movies = movieService.findByTitle(title);
        return ResponseEntity.ok(movies);
    }
}

