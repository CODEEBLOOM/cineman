package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.GenresRequest;
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

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Genre Controller")
@RequestMapping("/admin/genre")
public class GenreAController {

    private final GenreService genreService;
    private Map<String, Object> response;

    @Operation(summary = "Get all genre", description = "API dùng để lấy ra toàn bộ thể loại phim có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllGenres() {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Success");
        response.put("data", genreService.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get all genre", description = "API dùng để lấy ra thể loại phim theo id có trong hệ thống.")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGenreById(@PathVariable @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Success");
        response.put("data", genreService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Create new genre", description = "API dùng để tạo mới thể loại phim có trong hệ thống.")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createNewGenre(@RequestBody @Valid GenresRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Create new genre successfully");
        response.put("data", genreService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update genre", description = "API dùng để cập nhật thể loại phim có trong hệ thống.")
    @PutMapping("/{id}/update")
    public ResponseEntity<Map<String, Object>> updateGenre(
            @PathVariable  @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id,
            @RequestBody @Valid GenresRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Update genre successfully");
        response.put("data", genreService.update(id, request));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Update genre", description = "API dùng để cập nhật thể loại phim có trong hệ thống.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteGenre(
            @PathVariable  @Min(value = 1, message = "Id's genre is must be greater than 0") Integer id) {
        response = new LinkedHashMap<>();
        response.put("data", null);
        if(genreService.delete(id) == 1 ){
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Delete genre successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Genre cannot be deleted ! ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
