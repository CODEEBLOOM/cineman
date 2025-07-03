package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.SeatRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/seat")
@Tag(name = "Seat Controller")
@Validated
public class SeatAController {

    private final SeatService seatService;

    @Operation(summary = "Get all seat", description = "API dùng để lấy ra toàn bộ ghế của rạp chiếu theo id.")
    @GetMapping("/cinema-theater/{id}/all")
    public ResponseEntity<ApiResponse> getAllSeatByCinemaTheater(
            @PathVariable @Min(value = 1, message = "Id của phòng chiếu phải lớn hơn 0 !") Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatService.findAllByCinemaTheater(id))
                        .build()
        );
    }

    @Operation(summary = "Get all seat by id", description = "API dùng để lấy ra toàn bộ ghế theo id và rạp chiếu theo id.")
    @GetMapping("{id}/cinema-theater/{cinemaTheaterId}")
    public ResponseEntity<ApiResponse> getSeatByCinemaTheater(
            @PathVariable @Min(value = 1, message = "Id của ghế phải lớn hơn 0 !") Long id,
            @PathVariable @Min(value = 1, message = "Id của phòng chiếu phải lớn hơn 0 !") Integer cinemaTheaterId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatService.findById(cinemaTheaterId, id))
                        .build()
        );
    }

    @Operation(summary = "Create seat", description = "API dùng để tạo mới ghế cho rsạp chiếu.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createSeatByCinemaTheater(
            @RequestBody @Valid SeatRequest request
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Create seat success")
                        .data(seatService.save(request))
                        .build()
        );
    }

    @Operation(summary = "Create multiple seat", description = "API dùng để tạo mới hàng loạt ghế cho rsạp chiếu.")
    @PostMapping("/add-mul")
    public ResponseEntity<ApiResponse> createMulSeatByCinemaTheater(
            @RequestBody @Valid List<SeatRequest> seatRequests
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Create seat success")
                        .data(seatService.addMultiple(seatRequests))
                        .build()
        );
    }

    @Operation(summary = "Update status seat", description = "API dùng để cập nhật thông tin ghế cho rạp chiếu.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateSeatByCinemaTheater(
            @PathVariable @Min(value = 1, message = "Id của ghế phải lớn hơn 0 !") Long id,
            @RequestBody @Valid SeatRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update seat success")
                        .data(seatService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Update status seat", description = "API dùng để cập nhật thông tin ghế cho rạp chiếu.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteSeatByCinemaTheater(
            @PathVariable @Min(value = 1, message = "Id của ghế phải lớn hơn 0 !") Long id
    ) {
        seatService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete seat success")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Delete multiple seat", description = "API dùng để xóa nhiều ghế cho rạp chiếu.")
    @DeleteMapping("/delete-mul")
    public ResponseEntity<ApiResponse> deleteMulSeatByCinemaTheater(
            @RequestBody  List<Long> ids
    ) {
        seatService.deleteMultiple(ids);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete multiple seat success")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Change status seat", description = "API dùng cập nhật trạng thái ghế cho phòng chiếu đã được chọn.")
    @PutMapping("/{id}/change-status")
    public ResponseEntity<ApiResponse> changeStatusSeat(
            @PathVariable @Min(value = 1, message = "Id của ghế phải lớn hơn 0 !") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("change status seat success")
                        .data(seatService.changeStatus(id))
                        .build()
        );
    }

}
