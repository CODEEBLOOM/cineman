package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieTheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/movie-theater")
@Tag(name = "Movie Theater Controller", description = "Quản lý rạp chiếu phim.")
public class MovieTheaterAController {

    private final MovieTheaterService movieTheaterService;

    @Operation(summary = "Add movie theater", description = "API dùng để tạo mới rạp chiếu phim.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createMovieThear(@RequestBody @Valid MovieTheaterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Add movie theater success")
                        .data(movieTheaterService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Update movie theater", description = "API dùng để cập nhật thông tin rạp chiếu phim.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateMovieTheater(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid MovieTheaterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update movie theater success")
                        .data(movieTheaterService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete movie theater", description = "API dùng để xóa rạp chiếu phim.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteMovieTheater(
            @PathVariable @Min(1) Integer id) {
        movieTheaterService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update movie theater success")
                        .data(null)
                        .build()
        );
    }


    @Operation(summary = "Find all movie theater", description = "API dùng để lấy tất cả thông tin rạp chiếu phim.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAllMovieTheater(
            PageRequest pageRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieTheaterService.findAllByPage(pageRequest))
                        .build()
        );
    }


    @Operation(summary = "Find movie theater by id", description = "API dùng để lấy thông tin rạp chiếu phim theo id.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findMovieTheaterById(
            @PathVariable @Min(1) Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieTheaterService.findById(id))
                        .build()
        );
    }


}
