package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.CinemaTypeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.CinemaTypeService;
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
@Tag(name = "Cinema Type Controller")
@RequestMapping("${api.path}/admin/cinema-type")
@Validated
public class CinemaTypeController {

    private final CinemaTypeService cinemaTypeService;

    @Operation(summary = "Get all cinema type", description = "API dùng để lấy ra toàn bộ loại rạp chiếu có trong hệ thống.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCinemaType() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(cinemaTypeService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Get cinema type by id", description = "API dùng để lấy ra rạp chiếu theo id trong hệ thống.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAllCinemaTypeById(
            @PathVariable @Min(1) Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(cinemaTypeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create cinema type", description = "API dùng để tạo mới loại rạp chiếu có trong hệ thống.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createCinemaType(@RequestBody @Valid CinemaTypeRequest cinemaTypeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create cinema type successfully")
                        .data(cinemaTypeService.create(cinemaTypeRequest))
                        .build()
        );
    }

    @Operation(summary = "Create cinema type", description = "API dùng để cập nhật thông tin loại rạp chiếu có trong hệ thống.")
    @PostMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateCinemaType(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid CinemaTypeRequest cinemaTypeRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update cinema type successfully")
                        .data(cinemaTypeService.update(id, cinemaTypeRequest))
                        .build()
        );
    }

    @Operation(summary = "Create cinema type", description = "API dùng để tạo mới loại rạp chiếu có trong hệ thống.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteCinemaType(
            @PathVariable @Min(1) Integer id) {
        cinemaTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete cinema type successfully")
                        .data(null)
                        .build()
        );
    }

}
