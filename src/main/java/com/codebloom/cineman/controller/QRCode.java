package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.QRCodeService;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/qrcode")
@Validated
@RequiredArgsConstructor
@Tag(name = "QRCode", description = "QRCode")
public class QRCode {

    private final QRCodeService qrCodeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(
            @RequestParam("ticketCode") String ticketCode
    ) throws IOException, WriterException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Create QRCode success")
                .data(qrCodeService.generateQRCode(ticketCode))
                .build());
    }
}
