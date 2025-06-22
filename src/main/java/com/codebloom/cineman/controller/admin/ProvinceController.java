package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.ProvinceRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.ProvinceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@Tag(name = "Province Controller")
@RequestMapping("${api.path}/admin/province")
@RestController
@Validated
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping("/all")
    @Operation(summary = "Get all province", description = "API dùng lấy toàn bộ tỉnh thành có rạp Cineman ")
    public ResponseEntity<ApiResponse> getAllProvince() {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(provinceService.findAll())
                        .build()
        );
    }

    @GetMapping("/{id}/id")
    @Operation(summary = "Get province by id", description = "API dùng lấy tỉnh thành theo id ")
    public ResponseEntity<ApiResponse> getProvinceById(@PathVariable @NotNull @Min(1) Integer id) {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(provinceService.findById(id))
                        .build()
        );
    }

    @GetMapping("/{name}/name")
    @Operation(summary = "Get province by name", description = "API dùng lấy tỉnh thành theo tên ")
    public ResponseEntity<ApiResponse> getProvinceByName(@PathVariable @NotNull @NotBlank String name) {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(provinceService.findByName(name))
                        .build()
        );
    }

    @PostMapping("/add")
    @Operation(summary = "Create a province", description = "API dùng lấy tạo mới tỉnh thành ")
    public ResponseEntity<ApiResponse> createProvince(@RequestBody @Valid ProvinceRequest request) {
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .status(CREATED.value())
                        .message("Success")
                        .data(provinceService.save(request))
                        .build()
        );
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Update Province", description = "API dùng cập nhật thông tin tỉnh thành ")
    public ResponseEntity<ApiResponse> updateProvince(
            @PathVariable @NotNull @Min(1) Integer id,
            @RequestBody @Valid ProvinceRequest request) {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(provinceService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete Province", description = "API dùng xóa thông tin tỉnh thành ")
    public ResponseEntity<ApiResponse> deleteProvince(
            @PathVariable @NotNull @Min(1) Integer id) {
        provinceService.delete(id);
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{code}/code/delete")
    @Operation(summary = "Delete Province", description = "API dùng xóa thông tin tỉnh thành ")
    public ResponseEntity<ApiResponse> deleteProvinceByCode(
            @PathVariable @NotNull @Min(0) Integer code) {
        provinceService.deleteByCode(code);
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }

}
