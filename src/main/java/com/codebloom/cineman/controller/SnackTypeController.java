package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.SnackService;
import com.codebloom.cineman.service.SnackTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}/snack-type")
@RequiredArgsConstructor
@Tag(name = "Snack Client Controller")
@Validated
public class SnackTypeController {

    private final SnackTypeService snackTypeService;

    @Operation(summary = "Lấy tất cả snack type", description = "Lấy tất cả snack type")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllComboSnacksBySnackTypeId() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy tất cả snack là combo thành công.")
                        .status(HttpStatus.OK.value())
                        .data(snackTypeService.findAll())
                        .build()
        );
    }

}
