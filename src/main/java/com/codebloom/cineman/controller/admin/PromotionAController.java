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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.path}/admin/promotion")
@Tag(name = "Promotion Controller Admin", description = "Quản lý khuyến mãi")
public class PromotionAController {

    private final PromotionService promotionService;


    @Operation(summary = "Tạo một khuyến mãi", description = "Tạo khuyến mãi -- status INACTIVE")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createPromotion(@RequestBody @Valid PromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Tạo khuyến mãi thành công")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Cập nhật thông tin khuyến mãi", description = "Cập nhật thông tin khuyến mãi")
    @PostMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updatePromotion(
            @PathVariable @Min( value = 1 , message = "Id của promotion phải lớn hơn 1 !") Long id,
            @RequestBody @Valid PromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Cập nhật khuyến mãi thành công")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionService.update(id,request))
                        .build()
        );
    }

    @Operation(summary = "Áp dụng khuyến mãi ", description = "Áp dụng khuyến mãi cho khách hàng sử dụng")
    @PostMapping("/{id}/apply")
    public ResponseEntity<ApiResponse> applyPromotion(
            @PathVariable @Min( value = 1 , message = "Id của promotion phải lớn hơn 1 !") Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Áp dụng khuyến mãi cho khách hàng thành công")
                        .status(HttpStatus.CREATED.value())
                        .data(promotionService.activePromotion(id))
                        .build()
        );
    }

    @Operation(summary = "Xóa khuyến mãi", description = "Xóa khuyến mái")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deletePromotion(
            @PathVariable @Min( value = 1 , message = "Id của promotion phải lớn hơn 1 !") Long id) {
        promotionService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Xóa khuyến mãi thành công")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @Operation(summary = "Tìm kiếm khuyến mãi theo id", description = "Tìm kiếm khuyến mãi theo id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findPromotionById(
            @PathVariable @Min( value = 1 , message = "Id của promotion phải lớn hơn 1 !") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Xóa khuyến mãi thành công")
                        .status(HttpStatus.OK.value())
                        .data(promotionService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Tìm kiếm khuyến mãi", description = "Tìm kiếm khuyến mãi")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAll(
            @RequestParam(required = false) StatusPromotion status
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Xóa khuyến mãi thành công")
                        .status(HttpStatus.OK.value())
                        .data(promotionService.findAll(status))
                        .build()
        );
    }

}
