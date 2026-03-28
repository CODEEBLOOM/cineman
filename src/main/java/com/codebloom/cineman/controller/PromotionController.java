package com.codebloom.cineman.controller;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.path}/promotion")
@Tag(name = "Promotion Controller Customer")
@Validated
public class PromotionController {

    private final PromotionService promotionService;

    @Operation(summary = "Kiem tra voucher")
    @PutMapping("/{code}/amount/{amount}/apply")
    public ResponseEntity<ApiResponse> applyPromotion(
            @PathVariable("code") @NotNull(message = "Code giam gia khong duoc phep null !") String code,
            @PathVariable("amount") @Min(value = 0, message = "So tien phai lon hon 0 !") Double amount
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Apply promotion successfully")
                        .data(promotionService.applyPromotion(code, amount))
                        .build()
        );
    }

    @Operation(summary = "Huy ap dung voucher")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelPromotion(
            @PathVariable("id") @NotNull(message = "Id giam gia khong duoc phep null !") Long id
    ) {
        promotionService.cancelPromotion(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Apply promotion successfully")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Revert quantity promotion", description = "Api dung de cong lai so luong mot ma giam gia vi thanh toan that bai.")
    @PutMapping("/revert-quantity/invoice/{vnp_TxnRef}")
    public ResponseEntity<ApiResponse> revertQuantityPromotion(
            @PathVariable @NotNull(message = "vnp_TxnRef khong duoc phep null !") String vnp_TxnRef
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(promotionService.returnQuantityPromotion(vnp_TxnRef))
                        .build()
        );
    }

    @Operation(summary = "Lay danh sach khuyen mai cua nguoi dung", description = "Co the loc theo trang thai ACTIVE hoac USED.")
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<ApiResponse> getAllPromotion(
            @PathVariable @Min(value = 1, message = "userId khong duoc nho hon 1 !") Long userId,
            @RequestParam(required = false) StatusPromotion status
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(promotionService.findAllPromotionByUserId(userId, status))
                        .build()
        );
    }
}
