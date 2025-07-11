package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.MovieRoleRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
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


@RestController
@RequestMapping("${api.path}/admin/movie-role")
@RequiredArgsConstructor
@Tag(name = "Movie Role Controller", description = "Quản lý vai trò tham gia của một phim")
@Validated
public class MovieRoleAController {

    private final MovieRoleService movieRoleService;


    @Operation(summary = "Get all movie role", description = "API dùng để lấy toàn bộ vai trò tham gia của một phim")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovieRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieRoleService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Get movie role by id", description = "API dùng để lấy vai trò tham gia của một phim theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieRoleById( @PathVariable @Min(value = 1, message = "Id of movie role is must be greater than 0") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(movieRoleService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create Movie Role", description = "API dùng để tạo mới vai trò tham gia của một phim theo ID")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createMovieRole(@RequestBody @Valid MovieRoleRequest movieRoleRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create movie role successfully")
                        .data(movieRoleService.create(movieRoleRequest))
                        .build()
        );
    }

    @Operation(summary = "Create Movie Role", description = "API dùng để tạo mới vai trò tham gia của một phim theo ID")
    @PutMapping("/{movieRoleId}/update")
    public ResponseEntity<ApiResponse> updateMovieRole(
            @PathVariable  @Min(value = 1, message = "Id of movie role is must be greater than 0") Integer movieRoleId,
            @RequestBody @Valid MovieRoleRequest movieRoleRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update movie role successfully")
                        .data(movieRoleService.update(movieRoleId, movieRoleRequest))
                        .build()
        );
    }

    @Operation(summary = "Delete Movie Role", description = "API dùng để xóa vai trò tham gia của một phim theo ID")
    @DeleteMapping("/{movieRoleId}/delete")
    public ResponseEntity<ApiResponse> deleteMovieRole(
            @PathVariable  @Min(value = 1, message = "Id of movie role is must be greater than 0") Integer movieRoleId) {
        movieRoleService.delete(movieRoleId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete movie role successfully")
                        .data(null)
                        .build()
        );
    }

}
