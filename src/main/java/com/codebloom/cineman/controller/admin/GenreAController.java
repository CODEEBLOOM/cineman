package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.GenreService;
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
@RequiredArgsConstructor
@Validated
@Tag(name = "Genre of movie Controller")
@RequestMapping("${api.path}/admin/genre")
public class GenreAController {

    private final GenreService genreService;

    @Operation(summary = "Get all genre", description = "API dùng để lấy ra toàn bộ thể loại phim có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllGenres() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(genreService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Get all genre", description = "API dùng để lấy ra thể loại phim theo id có trong hệ thống.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getGenreById(@PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(genreService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create new genre", description = "API dùng để tạo mới thể loại phim có trong hệ thống.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createNewGenre(@RequestBody @Valid GenresRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create new genre successfully")
                        .data(genreService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Update genre", description = "API dùng để cập nhật thể loại phim có trong hệ thống.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateGenre(
            @PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id,
            @RequestBody @Valid GenresRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update genre successfully")
                        .data(genreService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Update genre", description = "API dùng để cập nhật thể loại phim có trong hệ thống.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteGenre(
            @PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id) {
        genreService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete genre successfully")
                        .data(null)
                        .build()
        );
    }


}
