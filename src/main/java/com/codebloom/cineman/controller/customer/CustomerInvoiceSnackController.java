package com.codebloom.cineman.controller.customer;

import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.DetailBookingSnackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}/invoices/detail-snacks")
@Validated
@RequiredArgsConstructor
@Tag(name = "Invoice Snack (Customer)")
public class CustomerInvoiceSnackController {

    private final DetailBookingSnackService detailBookingSnackService;

    @Operation(summary = "Add Snack to Invoice")
    @PostMapping("/{id}/add")
    public ResponseEntity<ApiResponse> addToInvoice(@PathVariable @Min(1) Long id,
                                                    @Valid @RequestBody DetailBookingSnackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Snack added to Invoice successfully")
                        .data(detailBookingSnackService.addToInvoice(id, request))
                        .build()
        );
    }
}
