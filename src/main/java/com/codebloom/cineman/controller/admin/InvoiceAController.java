package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.controller.response.InvoiceDetailPageResponse;
import com.codebloom.cineman.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Invoice Controller", description = "API dùng để quản lý hoa đơn.")
@RequestMapping("${api.path}/admin/invoice")
public class InvoiceAController {

    private final InvoiceService invoiceService;

    @Operation(summary = "Get all invoies by date", description = "API dùng để lấy ra toàn bộ hóa đơn theo ngày cơ bản.")
    @GetMapping("/all/date/{date}")
    public ResponseEntity<ApiResponse> getAllInvoicesByShowDate(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "movieTheaterId") Optional<Integer> movieTheaterId

    ) {
        InvoiceDetailPageResponse invoiceDetailPageResponse = null;
        if(movieTheaterId.isPresent()) {
            invoiceDetailPageResponse = invoiceService
                    .findAllInvoicesByShowDateAndMovieTheater(date, pageNo, pageSize, movieTheaterId.get());
        }else {
            invoiceDetailPageResponse = invoiceService
                    .findAllInvoicesByShowDateAndMovieTheater(date, pageNo, pageSize);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(invoiceDetailPageResponse)
                        .build()
        );
    }

    @Operation(summary = "Find invoice by QR Code", description = "Api dùng để lấy thông tin hóa đơn theo QR Code")
    @GetMapping("/qr-code/{qrCode}")
    public ResponseEntity<ApiResponse> findByQrCode(
            @PathVariable String qrCode
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(invoiceService.findByQrCode(qrCode))
                        .build()
        );
    }

    @Operation(summary = "Update Status Used", description = "Api dùng để cập nhật trạng thái hóa đơn")
    @PutMapping("/qr-code/{qrCode}")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable String qrCode
    ) {
        invoiceService.updateStatusUsed(qrCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(null)
                        .build()
        );
    }

}
