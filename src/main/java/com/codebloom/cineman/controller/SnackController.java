package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SnackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}/snack")
@RequiredArgsConstructor
@Tag(name = "Snack Client Controller")
@Validated
public class SnackController {

    private final SnackService snackService;

    @Operation(summary = "Lấy tất cả snack là combo", description = "Lấy tất cả snack là combo")
    @GetMapping("/combo/all")
    public ResponseEntity<ApiResponse> getAllComboSnacks() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy tất cả snack là combo thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackService.findAllComboSnacks())
                        .build()
        );
    }

}
