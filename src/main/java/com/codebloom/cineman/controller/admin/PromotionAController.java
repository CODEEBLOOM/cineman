package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("${api.path}/admin/promotion")
@Tag(name = "Promotion Controller Admin", description = "Quan ly khuyen mai")
public class PromotionAController {

    private final PromotionService promotionService;

    @Operation(summary = "Tao mot khuyen mai", description = "Tao khuyen mai voi trang thai mac dinh la INACTIVE")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createPromotion(@RequestBody @Valid PromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Tao khuyen mai thanh cong")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Cap nhat thong tin khuyen mai", description = "Cap nhat thong tin khuyen mai")
    @PostMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updatePromotion(
            @PathVariable @Min(value = 1, message = "Id cua promotion phai lon hon 0") Long id,
            @RequestBody @Valid PromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Cap nhat khuyen mai thanh cong")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Kich hoat khuyen mai", description = "Kich hoat khuyen mai cho khach hang su dung")
    @PostMapping("/{id}/apply")
    public ResponseEntity<ApiResponse> applyPromotion(
            @PathVariable @Min(value = 1, message = "Id cua promotion phai lon hon 0") Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Kich hoat khuyen mai thanh cong")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionService.activePromotion(id))
                        .build()
        );
    }

    @Operation(summary = "Xoa khuyen mai", description = "Xoa mem khuyen mai")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deletePromotion(
            @PathVariable @Min(value = 1, message = "Id cua promotion phai lon hon 0") Long id) {
        promotionService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Xoa khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @Operation(summary = "Tim kiem khuyen mai theo id", description = "Lay thong tin chi tiet khuyen mai")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findPromotionById(
            @PathVariable @Min(value = 1, message = "Id cua promotion phai lon hon 0") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Tim khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .data(promotionService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Tim kiem khuyen mai", description = "Lay danh sach khuyen mai")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAll(@RequestParam(required = false) StatusPromotion status) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Lay danh sach khuyen mai thanh cong")
                        .status(HttpStatus.OK.value())
                        .data(promotionService.findAll(status))
                        .build()
        );
    }
}
