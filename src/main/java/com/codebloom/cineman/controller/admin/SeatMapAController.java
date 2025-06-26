package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.SeatMapRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SeatMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Seat Map Controller")
@RequestMapping("${api.path}/admin/seat-map")
@Validated
public class SeatMapAController {

    private final SeatMapService seatMapService;

    @Operation(summary = "Get all seat map", description = "API dùng để lấy ra toàn bộ sơ đồ ghế có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSeatMap(PageRequest request, @RequestParam Map<String, Object> requestParams) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatMapService.findAll(request, requestParams))
                        .build()
        );
    }

    @Operation(summary = "Get seat map by id", description = "API dùng để lấy ra toàn bộ sơ đồ ghế có trong hệ thống.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSeatMap(
            @PathVariable @Min(value = 1 , message = "Id của sơ đồ ghế phải lớn hơn 0 !") Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatMapService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Tạo mới sơ đồ ghế", description = "API dùng để tạo mới sơ đồ ghế có trong hệ thống.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createSeatMap(
            @RequestBody @Valid SeatMapRequest seatMapRequest
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tạo mới thành công")
                        .data(seatMapService.save(seatMapRequest))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật sơ đồ ghế", description = "API dùng để cập nhật toàn bộ sơ đồ ghế có trong hệ thống.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateSeatMap(
            @PathVariable @Min(value = 1 , message = "Id của sơ đồ ghế phải lớn hơn 0 !") Integer id,
            @RequestBody @Valid SeatMapRequest seatMapRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatMapService.update(id, seatMapRequest))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật sơ đồ ghế", description = "API dùng để cập nhật toàn bộ sơ đồ ghế có trong hệ thống.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteSeatMap(
            @PathVariable @Min(value = 1 , message = "Id của sơ đồ ghế phải lớn hơn 0 !") Integer id
    ) {
        seatMapService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }

}
