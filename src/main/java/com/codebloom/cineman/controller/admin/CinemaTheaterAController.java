package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.controller.request.CinemaTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.CinemaTheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cinema Theater Controller")
@RequestMapping("${api.path}/admin/cinema-theater")
@Validated
public class CinemaTheaterAController {

    private final CinemaTheaterService cinemaTheaterService;

    @Operation(summary = "Get all cinema theater", description = "API dùng để lấy ra toàn phòng chiếu có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCinemaTheater(PageRequest pageRequest, @RequestParam Optional<CinemaTheaterStatus> status) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(status.isPresent()?cinemaTheaterService.findAll(pageRequest, status.get()) :cinemaTheaterService.findAll(pageRequest) )
                        .build()
        );
    }

    @Operation(summary = "Get cinema theater by id", description = "API dùng để lấy ra phòng chiếu theo id trong hệ thống.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAllCinemaTheaterById(
            @PathVariable @Min(1) Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(cinemaTheaterService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create cinema theater", description = "API dùng để tạo mới loại phòng chiếu có trong hệ thống.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createCinemaType(@RequestBody @Valid CinemaTheaterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create cinema theater successfully")
                        .data(cinemaTheaterService.create(req))
                        .build()
        );
    }

    @Operation(summary = "Create cinema theater", description = "API dùng để cập nhật thông tin phòng chiếu có trong hệ thống.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateCinemaType(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid CinemaTheaterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update cinema type successfully")
                        .data(cinemaTheaterService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Create cinema theater", description = "API dùng để xóa phòng chiếu có trong hệ thống.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteCinemaType(
            @PathVariable @Min(1) Integer id) {
        cinemaTheaterService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete cinema theater successfully")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Get seat map by cinema theater id", description = "API dùng để thông tin sơ đồ ghế phòng chiếu có trong hệ thống.")
    @GetMapping("/{id}/seat-map")
    public ResponseEntity<ApiResponse> getSeatMap(
            @PathVariable @Min(1) Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete cinema theater successfully")
                        .data(cinemaTheaterService.findSeatMapByCinemaTheaterId(id))
                        .build()
        );
    }

    @Operation(summary = "Get seat map by cinema theater id", description = "API dùng để thông tin sơ đồ ghế phòng chiếu có trong hệ thống.")
    @PutMapping("/{id}/published")
    public ResponseEntity<ApiResponse> publishedCinemaTheater(
            @PathVariable @Min(1) Integer id) {
        cinemaTheaterService.publishedCinemaTheater(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Published cinema theater successfully")
                        .data(null)
                        .build()
        );
    }

}
