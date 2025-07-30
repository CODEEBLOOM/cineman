package com.codebloom.cineman.controller;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.InvoiceService;

@RestController
@RequestMapping("${api.path}/invoice")
@RequiredArgsConstructor
@Tag(name = "Invoice Client Controller")
@Validated
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "Create invoice", description = "Api dùng để tạo một hóa đơn.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createInvoice(
            @RequestBody @Validated InvoiceCreateRequest req
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.createInvoice(req))
                        .build()
        );
    }

    @Operation(summary = "Update invoice", description = "Api dùng để cập nhật thông tin hóa đơn.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateInvoice(
            @PathVariable @Min(value = 1, message = "Id's invoice is must be greater than 0 !") Long id,
            @RequestBody @Valid InvoiceUpdateRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.update(id, req))
                        .build()
        );
    }

    @Operation(summary = "Update VN_TxnRef", description = "Api dùng để cập nhật thông tin hóa đơn.")
    @PutMapping("/{id}/update-tnx")
    public ResponseEntity<ApiResponse> updateTnxInvoice(
            @PathVariable @Min(value = 1, message = "Id's invoice is must be greater than 0 !") Long id,
            @RequestParam("txnRef") @NotNull(message = "VN_TxnRef is required") @NotBlank(message = "VN_TxnRef is not blank") String txnRef) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.updateTnx(id, txnRef))
                        .build()
        );
    }

    @Operation(summary = "Get update status success", description = "Api dùng để cập nhật thông tin hóa đơn.")
    @PutMapping("/{txnRef}/update-status-success")
    public ResponseEntity<ApiResponse> updateStatusInvoice(
            @PathVariable @Min(value = 1, message = "Id's invoice is must be greater than 0 !") String txnRef) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.updateStatusPaymentSuccess(txnRef))
                        .build()
        );
    }


    @Operation(summary = "Get invoice", description = "Api dùng để lấy thông tin hóa đơn theo userId, showTimeId, trang thái.")
    @GetMapping("/user/{userId}/show-time/{showTimeId}")
    public ResponseEntity<ApiResponse> findByUserIdAndShowTimeIdAndStatus(
            @PathVariable @Min(value = 1, message = "Id's user is must be greater than 0 !") Long userId,
            @PathVariable @Min(value = 1, message = "Id's show time is must be greater than 0 !") Long showTimeId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.findByUserIdAndShowTimeId(userId, showTimeId))
                        .build()
        );
    }

    @Operation(summary = "Apply promotion to invoice", description = "Api dùng để áp dụng khuyến mãi cho hóa đơn.")
    @PutMapping("/{id}/promotion/{promotionId}/apply-promotion")
    public ResponseEntity<ApiResponse> applyPromotionToInvoice(
            @PathVariable @Min(value = 1, message = "Id's invoice is must be greater than 0 !") Long id,
            @PathVariable @Min(value = 1, message = "Id's promotion is must be greater than 0 !") Long promotionId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.applyPromotionToInvoice(id, promotionId))
                        .build()
        );
    }


}
