package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.ShowTimeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.ShowTimeService;
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
@RequestMapping("${api.path}/admin/show-time")
@Validated
@Tag(name = "ShowTime Controller", description = "Controller quản lý lịch chiếu phim")
@RequiredArgsConstructor
public class ShowTimeAController {

    private final ShowTimeService showTimeService;


    @Operation(summary = "Find all show time", description = "API dùng để lấy tất cả show time")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Find show time by id", description = "API dùng để lấy  show time theo id ")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findAllById(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Find show time by movie id", description = "API dùng để lấy  show time theo movie id ")
    @GetMapping("/movie/{id}")
    public ResponseEntity<ApiResponse> findAllByMovieId(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findShowTimeByMovieId(id))
                        .build()
        );
    }

    @Operation(summary = "Find show time by cinema theater", description = "API dùng để lấy  show time theo cinema theater id ")
    @GetMapping("/cinema-theater/{id}")
    public ResponseEntity<ApiResponse> findAllByCinemaTheaterId(
            @PathVariable @Min(value = 1, message = "Id's cinema theater is must be greater than 0") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(showTimeService.findShowTimeByCinemaTheaterId(id))
                        .build()
        );
    }

    @Operation(summary = "Add show time", description = "API dùng tạo lịch chiếu phim")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createShowTime(@RequestBody @Valid ShowTimeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Add show time success")
                        .data(showTimeService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Update show time", description = "API dùng cap nhật lịch chiếu phim")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateShowTime(
            @PathVariable @Min(1) Long id, @RequestBody @Valid ShowTimeRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update show time success")
                        .data(showTimeService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete show time", description = "API dùng xóa lịch chiếu phim")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteShowTime(@PathVariable @Min(1) Long id) {
        showTimeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete show time success")
                        .data(null)
                        .build()
        );
    }

}
