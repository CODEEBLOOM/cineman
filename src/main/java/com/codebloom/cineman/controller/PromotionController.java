package com.codebloom.cineman.controller;

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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.path}/promotion")
@Tag(name = "Promotion Controller Customer")
@Validated
public class PromotionController {

    private final PromotionService promotionService;


    @Operation(summary = "Kiểm tra voucher")
    @PutMapping("/{code}/amount/{amount}/apply")
    public ResponseEntity<ApiResponse> applyPromotion(
            @PathVariable("code") @NotNull(message = "Code giảm giá không được phép null !") String code,
            @PathVariable("amount") @Min(value = 0, message = "Số tiền phải lớn hơn 0 !") Double amount
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Apply promotion successfully")
                        .data(promotionService.applyPromotion(code, amount))
                        .build()
        );
    }

    @Operation(summary = "Kiểm tra voucher")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelPromotion(
            @PathVariable("id") @NotNull(message = "Id giảm giá không được phép null !") Long id
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

}
