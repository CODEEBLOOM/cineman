package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.PaymentDTO;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.VNPayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}/payment")
@Validated
@Tag( name = "Payment Controller", description = "Controller quản lý api thanh toán")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;

    @PostMapping("/create_payment_url")
    public ResponseEntity<ApiResponse> createPayment(@RequestBody PaymentDTO paymentRequest, HttpServletRequest request) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(paymentRequest, request);

            return ResponseEntity.ok(ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Payment URL generated successfully.")
                    .data(paymentUrl)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error generating payment URL: " + e.getMessage())
                            .build());
        }
    }

}
