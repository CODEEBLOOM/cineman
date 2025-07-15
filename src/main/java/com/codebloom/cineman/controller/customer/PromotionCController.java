package com.codebloom.cineman.controller.customer;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/customer/promotion")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customer Promotion Controller")
public class PromotionCController {

    private final PromotionService promotionService;

    @Operation(summary = "Lấy danh sách voucher còn hiệu lực", description = "Lấy tất cả voucher có trạng thái ACTIVE và nằm trong khoảng thời gian hợp lệ.")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailablePromotions() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách voucher còn hiệu lực")
                        .data(promotionService.getAvailablePromotions())
                        .build()
        );
    }

    @Operation(summary = "Kiểm tra mã voucher", description = "Dùng để kiểm tra mã voucher có hợp lệ hay không.")
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateVoucher(@RequestParam @NotBlank(message = "Voucher code is required") String code) {
        PromotionResponse response = promotionService.validateVoucherCode(code);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Mã hợp lệ")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Áp dụng mã voucher cho hóa đơn", description = "Chỉ áp dụng nếu tổng hóa đơn từ 400.000 trở lên.")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> applyVoucherToInvoice(
            @RequestParam @NotBlank(message = "Mã voucher không được để trống") String code,
            @RequestParam Long invoiceId) {

        promotionService.applyVoucherToInvoice(code, invoiceId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Áp dụng voucher thành công")
                        .build()
        );
    }

}
