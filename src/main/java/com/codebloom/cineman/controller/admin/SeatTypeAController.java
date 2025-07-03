package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.SeatTypeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SeatTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/seat-type")
@Tag(name = "Seat Type Controller")
@Validated
public class SeatTypeAController {

    private final SeatTypeService seatTypeService;

    @Operation(summary = "Get all seat type", description = "API dùng để lấy ra toàn bộ loại ghế của rạp chiếu.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSeatType() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatTypeService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Get seat type by id", description = "API dùng để lấy ra loại ghế theo id của rạp chiếu.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSeatTypeById(
            @PathVariable @Size(min = 1, max = 25, message = "Id của loại ghế có độ dài nhỏ hơn 26 kí tự !") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(seatTypeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create a seat type", description = "API dùng để tạo mới loại ghế của rạp chiếu.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createSeatType(@RequestBody SeatTypeRequest seatTypeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(seatTypeService.create(seatTypeRequest))
                        .build()
        );
    }

    @Operation(summary = "Update a seat type", description = "API dùng để cập nhật thông tin loại ghế của rạp chiếu.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateSeatType(
            @PathVariable @Size(min = 1, max = 25, message = "Id của loại ghế có độ dài nhỏ hơn 26 kí tự !") String id,
            @RequestBody SeatTypeRequest seatTypeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(seatTypeService.update(id, seatTypeRequest))
                        .build()
        );
    }

    @Operation(summary = "Delete a seat type", description = "API dùng để xóa loại ghế của rạp chiếu.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteSeatType(
            @PathVariable @Size(min = 1, max = 25, message = "Id của loại ghế có độ dài nhỏ hơn 26 kí tự !") String id
    ) {
        seatTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }

}
