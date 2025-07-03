package com.codebloom.cineman.controller.admin;


import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.service.PromotionService;
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
@RequestMapping("${api.path}/admin/promotion")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin Promotion Controller")
public class PromotionAController {

    private final PromotionService promotionService;

    @Operation(summary = "Lấy tất cả khuyến mãi", description = "Lấy danh sách tất cả khuyến mãi hiện có.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllPromotions() {
        List<PromotionResponse> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(promotions)
                        .build()
        );
    }

    @Operation(summary = "Tạo mới khuyến mãi", description = "API dùng để tạo mới một khuyến mãi.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createPromotion(@RequestBody @Valid PromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Created promotion successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật khuyến mãi", description = "API dùng để cập nhật một khuyến mãi.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updatePromotion(
            @PathVariable @Min(value = 1, message = "ID must be >= 1") Long id,
            @RequestBody @Valid PromotionRequest request
    ) {
        PromotionResponse response = promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Updated promotion successfully")
                        .data(response)
                        .build()
        );
    }

//    @Operation(summary = "Xoá khuyến mãi", description = "API dùng để xoá một khuyến mãi theo ID.")
//    @DeleteMapping("/{id}/delete")
//    public ResponseEntity<ApiResponse> deletePromotion(
//            @PathVariable @Min(value = 1, message = "ID must be >= 1") Long id
//    ) {
//        promotionService.deletePromotion(id);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
//                ApiResponse.builder()
//                        .status(HttpStatus.NO_CONTENT.value())
//                        .message("Deleted promotion successfully")
//                        .data(null)
//                        .build()
//        );
//    }

    @Operation(summary = "Lấy khuyến mãi theo ID", description = "API dùng để lấy chi tiết khuyến mãi theo ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPromotionById(
            @PathVariable @Min(value = 1, message = "ID must be >= 1") Long id
    ) {
        PromotionResponse response = promotionService.getPromotionById(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(response)
                        .build()
        );
    }
}