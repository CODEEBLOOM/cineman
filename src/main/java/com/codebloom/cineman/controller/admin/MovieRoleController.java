package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.MovieRoleRequest;
import com.codebloom.cineman.service.MovieRoleService;
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
@RequestMapping("/admin/movie-role")
@RequiredArgsConstructor
@Tag(name = "Movie Role Controller")
@Validated
public class MovieRoleController {

    private final MovieRoleService movieRoleService;
    private Map<String, Object> response;


    @Operation(summary = "Get all movie role", description = "API dùng để lấy toàn bộ vai trò tham gia của một phim")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMovieRoles() {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Success");
        response.put("data", movieRoleService.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get movie role by id", description = "API dùng để lấy vai trò tham gia của một phim theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMovieRoleById( @PathVariable @Min(value = 1, message = "Id of movie role is must be greater than 0") Integer id) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Success");
        response.put("data", movieRoleService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Create Movie Role", description = "API dùng để tạo mới vai trò tham gia của một phim theo ID")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createMovieRole(@RequestBody @Valid MovieRoleRequest movieRoleRequest) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Create movie role successfully");
        response.put("data", movieRoleService.create(movieRoleRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create Movie Role", description = "API dùng để tạo mới vai trò tham gia của một phim theo ID")
    @PutMapping("/{movieRoleId}/update")
    public ResponseEntity<Map<String, Object>> updateMovieRole(
            @PathVariable  @Min(value = 1, message = "Id of movie role is must be greater than 0") Integer movieRoleId,
            @RequestBody @Valid MovieRoleRequest movieRoleRequest) {
        response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Update movie role successfully");
        response.put("data", movieRoleService.update(movieRoleId, movieRoleRequest));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Delete Movie Role", description = "API dùng để xóa vai trò tham gia của một phim theo ID")
    @DeleteMapping("/{movieRoleId}/delete")
    public ResponseEntity<Map<String, Object>> deleteMovieRole(
            @PathVariable  @Min(value = 1, message = "Id of movie role is must be greater than 0") Integer movieRoleId) {
        response = new LinkedHashMap<>();
        response.put("data", null);
        if(movieRoleService.delete(movieRoleId) == 1){
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Delete movie role successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Movie role cannot be deleted !");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
