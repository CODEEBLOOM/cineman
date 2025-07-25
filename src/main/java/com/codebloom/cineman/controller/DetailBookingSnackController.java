package com.codebloom.cineman.controller;

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

import java.util.List;

@RestController
@RequestMapping("${api.path}/detail-booking-snack")
@Validated
@RequiredArgsConstructor
@Tag(name = "Detail Booking Snack", description = "Detail Booking Snack API")
public class DetailBookingSnackController {

    private final DetailBookingSnackService detailBookingSnackService;

    @Operation(summary = "Add Snack to Invoice")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addToInvoice( @RequestBody @Valid DetailBookingSnackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Snack added to Invoice successfully")
                        .data(detailBookingSnackService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Create multiple Snack to Invoice")
    @PutMapping("/create-multiple")
    public ResponseEntity<ApiResponse> createMultiple(@RequestBody @Valid List<DetailBookingSnackRequest> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Snack added to Invoice successfully")
                        .data(detailBookingSnackService.createMultiple(request))
                        .build()
        );
    }
}
