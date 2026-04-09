package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.PromotionTypeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.PromotionTypeService;
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
@Validated
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/promotion-type")
@Tag(name = "Promotion Type Controller Admin", description = "Quan ly loai khuyen mai")
public class PromotionTypeAController {

    private final PromotionTypeService promotionTypeService;

    @Operation(summary = "Tao moi loai khuyen mai")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid PromotionTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Tao loai khuyen mai thanh cong")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionTypeService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Cap nhat loai khuyen mai")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable @Min(value = 1, message = "Id cua loai khuyen mai phai lon hon 0") Long id,
            @RequestBody @Valid PromotionTypeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Cap nhat loai khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .data(promotionTypeService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Tim loai khuyen mai theo id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(
            @PathVariable @Min(value = 1, message = "Id cua loai khuyen mai phai lon hon 0") Long id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lay chi tiet loai khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .data(promotionTypeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Lay danh sach loai khuyen mai")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAll() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lay danh sach loai khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .data(promotionTypeService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Xoa mem loai khuyen mai")
    @DeleteMapping({"/{id}", "/{id}/delete"})
    public ResponseEntity<ApiResponse> delete(
            @PathVariable @Min(value = 1, message = "Id cua loai khuyen mai phai lon hon 0") Long id) {
        promotionTypeService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Xoa loai khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .data(id)
                        .build()
        );
    }
}
