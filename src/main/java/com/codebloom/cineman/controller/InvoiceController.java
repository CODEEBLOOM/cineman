package com.codebloom.cineman.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
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
    @PostMapping("/show-time/{showTimeId}/add")
    public ResponseEntity<ApiResponse> createInvoice(
            @RequestBody @Validated InvoiceCreateRequest req,
            @PathVariable @Min(value = 1, message = "Id's show time is must be greater than 0 !") Long showTimeId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.create(req, showTimeId))
                        .build()
        );
    }

    @Operation(summary = "Create invoice", description = "Api dùng để tạo một hóa đơn.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateInvoice(
            @PathVariable @Min(value = 1, message = "Id's invoice is must be greater than 0 !") Long id,
            @RequestBody @Validated InvoiceUpdateRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.update(id, req))
                        .build()
        );
    }

}
